package igentuman.nc.block.entity;

import igentuman.nc.block.ISizeToggable;
import igentuman.nc.content.storage.ContainerBlocks;
import igentuman.nc.handler.ItemStorageCapabilityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Supplier;

import static igentuman.nc.setup.registration.NCStorageBlocks.STORAGE_BE;

public class ContainerBE extends NuclearCraftBE implements ISizeToggable {

    public final ItemStorageCapabilityHandler inventory;
    private double loadRate = 0;

    private ItemStorageCapabilityHandler createInventory() {
        return new ItemStorageCapabilityHandler(ContainerBlocks.all().get(getName()).getCapacity(), 64);
    }

    public Supplier<ItemStorageCapabilityHandler> getItemHandler() {
        return itemHandler;
    }

    protected final Supplier<ItemStorageCapabilityHandler> itemHandler;

    public static final ModelProperty<HashMap<Integer, SideMode>> SIDE_CONFIG = new ModelProperty<>();

    public ContainerBE(BlockPos pPos, BlockState pBlockState) {
        super(STORAGE_BE.get(getName(pBlockState)).get(), pPos, pBlockState);
        for (Direction direction : Direction.values()) {
            sideConfig.put(direction.ordinal(), SideMode.DEFAULT);
        }
        inventory = createInventory();
        itemHandler = () -> inventory;
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
        transferItems();
        updateLoadRate();
    }

    private void updateLoadRate() {
        double wasRate = loadRate;
        loadRate = 0.05;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            loadRate += (stack.getCount() / (double) stack.getMaxStackSize()) / inventory.getSlots();
        }
        if (wasRate != loadRate) {
            setChanged();
            level.setBlockAndUpdate(worldPosition, getBlockState());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void transferItems() {
        if (level == null) return;
        for (Direction direction : Direction.values()) {
            if (sideConfig.get(direction.ordinal()) == SideMode.DISABLED) continue;

            IItemHandler cap = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.relative(direction), direction.getOpposite());
            if (cap != null) {
                boolean transactionDone = false;
                switch (sideConfig.get(direction.ordinal())) {
                    case OUT -> {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack stack = inventory.getStackInSlot(i);
                            if (stack.isEmpty()) continue;
                            ItemStack copy = stack.copy();
                            for (int j = 0; j < cap.getSlots(); j++) {
                                ItemStack left = cap.insertItem(j, copy, true);
                                if (left.getCount() < copy.getCount()) {
                                    cap.insertItem(j, copy, false);
                                    inventory.extractItem(i, copy.getCount() - left.getCount(), false);
                                    transactionDone = true;
                                    break;
                                }
                            }
                            if (transactionDone) break;
                        }
                    }
                    case IN -> {
                        for (int i = 0; i < cap.getSlots(); i++) {
                            ItemStack stack = cap.getStackInSlot(i);
                            if (stack.isEmpty()) continue;
                            ItemStack copy = stack.copy();
                            for (int j = 0; j < inventory.getSlots(); j++) {
                                ItemStack left = inventory.insertItem(j, copy, true);
                                if (left.getCount() < copy.getCount()) {
                                    inventory.insertItem(j, copy, false);
                                    cap.extractItem(i, copy.getCount() - left.getCount(), false);
                                    transactionDone = true;
                                    break;
                                }
                            }
                            if (transactionDone) break;
                        }
                    }
                }
            }
        }
    }

//    @Nonnull
//    @Override
//    public <T> Supplier<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        if (cap == Capabilities.ItemHandler.BLOCK && (side != null && sideConfig.get(side.ordinal()) != SideMode.DISABLED)) {
//            return getItemHandler().cast();
//        }
//        return super.getCapability(cap, side);
//    }

    @Override
    protected void saveClientData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag tank = new CompoundTag();
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putIntArray("sideConfig", sideConfig.values().stream().mapToInt(Enum::ordinal).toArray());
    }

    @Override
    public void loadClientData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(lookupProvider, tag.getCompound("Inventory"));
        }
        if (!tag.contains("sideConfig")) return;
        loadSideConfig(tag.getIntArray("sideConfig"));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
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
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putIntArray("sideConfig", sideConfig.values().stream().mapToInt(Enum::ordinal).toArray());
    }

    public ISizeToggable.SideMode toggleSideConfig(int direction) {
        sideConfig.put(direction, SideMode.values()[(sideConfig.get(direction).ordinal() + 1) % 4]);
        setChanged();
        level.setBlockAndUpdate(worldPosition, getBlockState());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return sideConfig.get(direction);
    }

    public String getTier() {
        return getName();
    }

    public int getRows() {
        return ContainerBlocks.all().get(getName()).getRows();
    }

    public int getColls() {
        return ContainerBlocks.all().get(getName()).getColls();
    }

    public double getLoadRate() {
        return loadRate;
    }
}
