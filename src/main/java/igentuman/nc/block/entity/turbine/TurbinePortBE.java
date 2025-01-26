package igentuman.nc.block.entity.turbine;

import igentuman.nc.NuclearCraft;
import igentuman.nc.handler.sided.capability.FluidCapabilityHandler;
import igentuman.nc.util.annotation.NBTField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TurbinePortBE extends TurbineBE {
    public static String NAME = "turbine_port";
    @NBTField
    public byte analogSignal = 0;
    @NBTField
    public byte comparatorMode = SignalSource.OVERFLOW;

    @NBTField
    public BlockPos controllerPos;

    public TurbinePortBE(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, NAME);
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public boolean hasRedstoneSignal() {
        return Objects.requireNonNull(getLevel()).hasNeighborSignal(worldPosition);
    }

    @Override
    public void tickServer() {
        if (NuclearCraft.instance.isNcBeStopped) return;
        super.tickServer();
        if (multiblock() == null || controller() == null) return;
        int wasSignal = analogSignal;
        boolean updated = sendOutPower();
        if (controllerPos == null) {
            controllerPos = controller().getBlockPos();
            updated = true;
            setChanged();
        }
        if (hasRedstoneSignal()) {
            controller().controllerEnabled = true;
        }

        updateAnalogSignal();

        updated = wasSignal != analogSignal || updated;

        Direction dir = getFacing();

        if (fluidHandler() != null) {
            updated = fluidHandler().pushFluids(dir, false, worldPosition) || updated;
            updated = fluidHandler().pullFluids(dir, false, worldPosition) || updated;
        }

        if (updated) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void updateAnalogSignal() {
        switch (comparatorMode) {
            case SignalSource.ENERGY:
                analogSignal = (byte) (controller().energyStorage.getEnergyStored() * 15 / controller().energyStorage.getMaxEnergyStored());
                break;

        }
    }

    protected FluidCapabilityHandler fluidHandler() {
        return controller().contentHandler.fluidCapability;
    }

    protected <T> IFluidHandler fluidHandler(@Nullable Direction side) {
        return controller().contentHandler.getFluidCapability(side);
    }

//    @Nonnull
//    @Override
//    public <T> Supplier<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        if (controller() == null) return super.getCapability(cap, side);
//
//        if (cap == Capabilities.FluidHandler.BLOCK) {
//            return fluidHandler(side).cast();
//        }
//        if (cap == Capabilities.EnergyStorage.BLOCK) {
//            return controller().getEnergy().cast();
//        }
//        if (isCcLoaded()) {
//            if (cap == dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL) {
//                return controller().getPeripheral(cap, side);
//            }
//        }
//        return super.getCapability(cap, side);
//    }

    protected boolean sendOutPower() {
        if (multiblock() == null) return false;
        AtomicInteger capacity = new AtomicInteger(controller().energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                IEnergyStorage cap = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK,worldPosition.relative(direction), direction.getOpposite());
                if (cap != null && cap.canReceive()){
                    int received = cap.receiveEnergy(Math.min(capacity.get(), controller().energyStorage.getMaxEnergyStored()), false);
                    capacity.addAndGet(-received);
                    controller().energyStorage.consumeEnergy(received);
                    setChanged();
                    if (capacity.get() < 0) break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canInvalidateCache() {
        return false;
    }

    @Override
    public TurbineControllerBE<?> controller() {
        if (NuclearCraft.instance.isNcBeStopped || (!getLevel().isClientSide() && getLevel().getServer() != null && !getLevel().getServer().isRunning())) return null;
        if (getLevel().isClientSide && controllerPos != null) {
            return (TurbineControllerBE<?>) getLevel().getBlockEntity(controllerPos);
        }
        try {
            return (TurbineControllerBE<?>) multiblock().controller().controllerBE();
        } catch (NullPointerException e) {
            if (controllerPos != null) {
                return (TurbineControllerBE<?>) getLevel().getBlockEntity(controllerPos);
            }
            return null;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("Info")) {
            CompoundTag infoTag = tag.getCompound("Info");
            readTagData(infoTag);
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag infoTag = new CompoundTag();
        saveTagData(infoTag);
        tag.put("Info", infoTag);
    }

    @Override
    public void loadClientData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (tag.contains("Info")) {
            CompoundTag infoTag = tag.getCompound("Info");
            readTagData(infoTag);
        }
    }

    @Override
    protected void saveClientData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag infoTag = new CompoundTag();
        tag.put("Info", infoTag);
        saveTagData(infoTag);
    }

    public int getEnergyStored() {
        if (controller() == null) return 0;
        return controller().energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        if (controller() == null) return 0;
        return controller().energyStorage.getMaxEnergyStored();
    }

    public int energyPerTick() {
        if (controller() == null) return 0;
        return controller().energyPerTick;
    }

    public void toggleComparatorMode() {
        comparatorMode++;
        if (comparatorMode > SignalSource.OVERFLOW) {
            comparatorMode = SignalSource.ENERGY;
        }
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public FluidTank getFluidTank(int i) {
        if (controller() == null) return null;
        return controller().getFluidTank(i);
    }

    public static class SignalSource {
        public static final byte ENERGY = 1;
        public static final byte OVERFLOW = 2;
    }
}
