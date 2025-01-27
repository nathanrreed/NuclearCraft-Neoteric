package igentuman.nc.block.entity.energy;

import igentuman.nc.block.entity.NuclearCraftBE;
import igentuman.nc.setup.registration.NCEnergyBlocks;
import igentuman.nc.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class NCEnergy extends NuclearCraftBE {

    protected String name;
    public static String NAME;
    public final CustomEnergyStorage energyStorage = createEnergy();

    public Supplier<IEnergyStorage> getEnergy() {
        return energy;
    }

    protected final Supplier<IEnergyStorage> energy = () -> energyStorage;

    protected int counter;

    protected void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
                if (be != null) {
                    IEnergyStorage handler = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(direction), direction.getOpposite());
                    if (handler != null && handler.canReceive()) {
                        int received = handler.receiveEnergy(Math.min(capacity.get(), getEnergyTransferPerTick()), false);
                        capacity.addAndGet(-received);
                        energyStorage.consumeEnergy(received);
                        setChanged();
                        if (capacity.get() <= 0) return;
                    }
                }
            }
        }
    }

    protected int getEnergyMaxStorage() {
        return 100;
    }

    protected int getEnergyTransferPerTick() {
        return Math.min(getEnergyMaxStorage(), energyStorage.getEnergyStored());
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(getEnergyMaxStorage(), getMaxTransfer(), getEnergyMaxStorage()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public int getMaxTransfer() {
        return 0;
    }

    public NCEnergy(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, String name) {
        super(pType, pPos, pBlockState);
    }

    public NCEnergy(BlockPos pPos, BlockState pBlockState, String name) {
        super(NCEnergyBlocks.ENERGY_BE.get(name).get(), pPos, pBlockState);
        this.name = name;
    }

    public void tickClient() {
    }

    public void tickServer() {
    }

    public String getName() {
        return name;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        invalidateCapabilities();
    }

    protected void saveClientData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag infoTag = new CompoundTag();
        saveTagData(infoTag);

        tag.put("Info", infoTag);

        tag.put("energy_storage", energyStorage.serializeNBT(registries));
        tag.putInt("energy", energyStorage.getEnergyStored());
    }

    public void loadClientData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("energy_storage")) {
            energyStorage.deserializeNBT(registries, tag.get("energy_storage"));
        }
        if (tag.contains("energy")) {
            energyStorage.setEnergy(tag.getInt("energy"));
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        int oldEnergy = energyStorage.getEnergyStored();

        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag, lookupProvider);
        if (oldEnergy != energyStorage.getEnergyStored()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("energy_storage")) {
            energyStorage.deserializeNBT(registries, tag.get("energy_storage"));
        }
        if (tag.contains("energy")) {
            energyStorage.setEnergy(tag.getInt("energy"));
        }

        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("energy_storage", energyStorage.serializeNBT(registries));
        tag.putInt("energy", energyStorage.getEnergyStored());
    }
}
