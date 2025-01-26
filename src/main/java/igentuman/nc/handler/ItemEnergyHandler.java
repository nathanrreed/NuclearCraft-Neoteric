package igentuman.nc.handler;

import igentuman.nc.util.CustomEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ItemEnergyHandler<O, C extends Direction, T extends ItemEnergyHandler.ItemEnergy> implements ICapabilityProvider<O, C, T> {
    private final int storage;
    private final int output;
    private final int input;

    protected final Supplier<ItemEnergy> energy = this::createEnergy;

    private ItemEnergy createEnergy() {
        return new ItemEnergy(stack, capacity(), chargeRate(), sendRate());
    }

    public int sendRate() {
        return output;
    }

    public int chargeRate() {
        return input;
    }

    public int capacity() {
        return storage;
    }

    public int getEnergyStored() {
        var object = getCapability((O) Capabilities.EnergyStorage.ITEM, null);
        if (object instanceof IEnergyStorage cap) {
            return cap.getEnergyStored();
        }
        return 0;
    }

    public ItemStack stack;

    public ItemEnergyHandler(ItemStack stack, int storage, int output, int input) {
        this.stack = stack;
        this.storage = storage;
        this.output = output;
        this.input = input;
    }

    @Override
    public @Nullable T getCapability(O cap, C side) {
        if (cap == Capabilities.EnergyStorage.ITEM) {
            return (T) energy.get();
        }
        return null;
    }

    public static class ItemEnergy extends CustomEnergyStorage {
        private ItemStack stack;

        public ItemEnergy(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
            super(capacity, maxReceive, maxExtract);
            this.stack = stack;

            energy = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("energy") ? stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("energy") : 0;
        }

        @Override
        public int extractEnergy(int extract, boolean simulate) {
            int amount = super.extractEnergy(extract, simulate);
            if (!simulate)
                stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putInt("energy", this.energy)));

            return amount;
        }

        @Override
        public int receiveEnergy(int receieve, boolean simulate) {
            int amount = super.receiveEnergy(receieve, simulate);
            if (!simulate)
                stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putInt("energy", this.energy)));

            return amount;
        }
    }
}