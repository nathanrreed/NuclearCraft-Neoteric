package igentuman.nc.block.entity;

import igentuman.nc.block.ISizeToggable;
import igentuman.nc.content.storage.BarrelBlocks;
import igentuman.nc.handler.sided.capability.NcFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static igentuman.nc.setup.registration.NCStorageBlocks.STORAGE_BE;

public class BarrelBE extends NuclearCraftBE implements ISizeToggable {

    public final NcFluidTank fluidTank;

    private NcFluidTank createTank() {
        return new NcFluidTank(BarrelBlocks.all().get(getName()).config().getCapacity()) {
            @Override
            public void setFluid(FluidStack fluid) {
                super.setFluid(fluid);
                setChanged();
            }

            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    public Supplier<IFluidHandler> getFluidHandler() {
        return fluidHandler;
    }

    protected final Supplier<IFluidHandler> fluidHandler;

    public static final ModelProperty<HashMap<Integer, SideMode>> SIDE_CONFIG = new ModelProperty<>();
    public boolean syncSideConfig = true;

    public BarrelBE(BlockPos pPos, BlockState pBlockState) {
        super(STORAGE_BE.get(getName(pBlockState)).get(), pPos, pBlockState);
        for (Direction direction : Direction.values()) {
            sideConfig.put(direction.ordinal(), SideMode.DEFAULT);
        }
        fluidTank = createTank();
        fluidHandler = () -> fluidTank;
    }

    @Nonnull
    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(SIDE_CONFIG, sideConfig)
                .build();
    }

    public void tickClient() {
    }

    public void tickServer() {
        transferFluid();
    }

    /**
     * Push pull fluids to adjacent blocks
     */
    protected void transferFluid() {
        AtomicInteger currentAmount = new AtomicInteger(fluidTank.getFluidAmount());
        boolean wasUpdated = false;
        for (Direction direction : Direction.values()) {
            if (
                    sideConfig.get(direction.ordinal()) == SideMode.DISABLED ||
                            sideConfig.get(direction.ordinal()) == SideMode.DEFAULT
            ) continue;
            BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
            if (be != null) {
                IFluidHandler sideHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, worldPosition.relative(direction), direction.getOpposite());
                if (sideHandler == null) continue;
                if (currentAmount.get() > 0 && sideConfig.get(direction.ordinal()) == SideMode.OUT) {
                    int accepted = sideHandler.fill(fluidTank.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
                    if (accepted > 0) {
                        fluidTank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                        wasUpdated = true;
                    }
                    currentAmount.addAndGet(-accepted);
                } else if (currentAmount.get() < getTankCapacity() && sideConfig.get(direction.ordinal()) == SideMode.IN) {
                    FluidStack drain = sideHandler.drain(fluidTank.getCapacity() - currentAmount.get(), IFluidHandler.FluidAction.SIMULATE);
                    if (drain.isEmpty()) continue;
                    if (drain.getFluid().isSame(fluidTank.getFluid().getFluid()) || currentAmount.get() == 0) {
                        int extracted = fluidTank.fill(drain, IFluidHandler.FluidAction.EXECUTE);
                        if (extracted > 0) {
                            sideHandler.drain(extracted, IFluidHandler.FluidAction.EXECUTE);
                            wasUpdated = true;
                        }
                        currentAmount.addAndGet(extracted);
                    }

                }
            }
        }
        if (wasUpdated) {
            level.setBlockAndUpdate(worldPosition, getBlockState());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private int getTankCapacity() {
        return BarrelBlocks.all().get(getName()).config().getCapacity();
    }

    protected void saveClientData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Fluid", fluidTank.getFluid().saveOptional(registries));
        tag.putIntArray("sideConfig", sideConfig.values().stream().mapToInt(Enum::ordinal).toArray());
    }

    public void loadClientData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("Fluid")) {
            fluidTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("Fluid")));
        }
        if (!tag.contains("sideConfig")) return;
        loadSideConfig(tag.getIntArray("sideConfig"));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Fluid")) {
            fluidTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("Fluid")));
        }
        if (!tag.contains("sideConfig")) return;
        loadSideConfig(tag.getIntArray("sideConfig"));
    }

    private void loadSideConfig(int[] tagData) {
        boolean changed = false;
        for (int i = 0; i < sideConfig.size(); i++) {
            SideMode newMode = SideMode.values()[tagData[i]];
            if (sideConfig.get(i) != newMode) {
                changed = true;
                sideConfig.remove(i);
                sideConfig.put(i, newMode);
            }

        }
        if (changed) {
            requestModelDataUpdate();
            if (level == null) return;
            level.setBlockAndUpdate(worldPosition, getBlockState());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Fluid", fluidTank.getFluid().saveOptional(registries));
        tag.putIntArray("sideConfig", sideConfig.values().stream().mapToInt(Enum::ordinal).toArray());
    }

    public SideMode toggleSideConfig(int direction) {
        sideConfig.put(direction, SideMode.values()[(sideConfig.get(direction).ordinal() + 1) % 4]);
        setChanged();
        level.setBlockAndUpdate(worldPosition, getBlockState());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return sideConfig.get(direction);
    }
}