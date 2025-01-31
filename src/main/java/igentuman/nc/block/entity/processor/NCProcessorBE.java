package igentuman.nc.block.entity.processor;

import dan200.computercraft.api.peripheral.IPeripheral;
import igentuman.nc.NuclearCraft;
import igentuman.nc.block.entity.NuclearCraftBE;
import igentuman.nc.compat.cc.NCProcessorPeripheral;
import igentuman.nc.compat.gt.NCGTEnergyHandler;
import igentuman.nc.compat.oc2.NCProcessorDevice;
import igentuman.nc.content.processors.ProcessorPrefab;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.handler.CatalystHandler;
import igentuman.nc.handler.UpgradesHandler;
import igentuman.nc.handler.sided.SidedContentHandler;
import igentuman.nc.handler.sided.SlotModePair;
import igentuman.nc.handler.sided.capability.ItemCapabilityHandler;
import igentuman.nc.radiation.data.RadiationManager;
import igentuman.nc.recipes.AbstractRecipe;
import igentuman.nc.recipes.NcRecipeType;
import igentuman.nc.recipes.RecipeInfo;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.type.NcRecipe;
import igentuman.nc.setup.registration.NCProcessors;
import igentuman.nc.util.CustomEnergyStorage;
import igentuman.nc.util.annotation.NBTField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static igentuman.nc.block.ProcessorBlock.ACTIVE;
import static igentuman.nc.handler.config.ProcessorsConfig.PROCESSOR_CONFIG;

public class NCProcessorBE<RECIPE extends AbstractRecipe> extends NuclearCraftBE {

    public static String NAME;
    public final SidedContentHandler contentHandler;
    protected final CustomEnergyStorage energyStorage;
    public HashMap<String, RECIPE> cachedRecipes = new HashMap<>();
    public final UpgradesHandler upgradesHandler = createUpgradesHandler();
    protected final Supplier<IItemHandler> handler = () -> upgradesHandler;
    public final CatalystHandler catalystHandler = createCatalystHandler();
    protected boolean saveSideMapFlag = true;
    public boolean wasUpdated = true;
    protected RECIPE recipe;
    public int manualUpdateCounter = 40;
    protected int skippedTicks = 1;
    private Supplier<NCProcessorPeripheral> peripheralCap;

    @NBTField
    public int speedMultiplier = 1;
    @NBTField
    public int energyPerTick = 0;
    @NBTField
    public int energyMultiplier = 1;
    @NBTField
    public int redstoneMode = 0;
    @NBTField
    public boolean isActive = false;

    private List<ItemStack> allowedInputs;
    private List<FluidStack> allowedFluids;
    private Supplier<NCGTEnergyHandler> gtEnergyCap;
    private ParticleOptions particle1 = ParticleTypes.SMOKE;
    protected ProcessorPrefab<?, ?> prefab;

    public Supplier<IEnergyStorage> getEnergy() {
        return energy;
    }

    protected final Supplier<IEnergyStorage> energy;

    public RecipeInfo<RECIPE> recipeInfo = new RecipeInfo<RECIPE>();

    public ProcessorPrefab<?, ?> prefab() {
        if (prefab == null) {
            prefab = Processors.all().get(getName());
        }
        return prefab;
    }

    public <T> IPeripheral getPeripheral(@Nonnull DeferredRegister<T> cap, @Nullable Direction side) {
        if (peripheralCap == null) {
            peripheralCap = () -> new NCProcessorPeripheral(this);
        }
        return peripheralCap.get();
    }

    @Override
    public ItemCapabilityHandler getItemInventory() {
        return contentHandler.itemHandler;
    }

    protected void updateRecipe() {
        recipe = getRecipe();
        if (recipe != null) {
            recipeInfo.setRecipe(recipe);
            recipeInfo.ticks = (int) (getBaseProcessTime() * recipe.getTimeModifier());
            recipeInfo.energy = getBasePower() * recipe.getEnergy();
            recipeInfo.radiation = recipeInfo.recipe.getRadiation();
            recipeInfo.be = this;
            recipe.consumeInputs(contentHandler);
        }
    }

    protected void addToCache(RECIPE recipe) {
        String key = contentHandler.getCacheKey();
        if (cachedRecipes.containsKey(key)) {
            cachedRecipes.replace(key, recipe);
        } else {
            cachedRecipes.put(key, recipe);
        }
    }

    public RECIPE getRecipe() {
        if (isInputEmpty()) return null;
        RECIPE cachedRecipe = getCachedRecipe();
        if (cachedRecipe != null) return cachedRecipe;
        if (!NcRecipeType.ALL_RECIPES.containsKey(getName())) return null;
        for (AbstractRecipe recipe : NcRecipeType.ALL_RECIPES.get(getName()).getRecipeType().getRecipes(getLevel())) {
            if (recipe.test(contentHandler)) {
                addToCache((RECIPE) recipe);
                return (RECIPE) recipe;
            }
        }
        return null;
    }

    private boolean isInputEmpty() {
        return contentHandler.isInputEmpty();
    }

    public RECIPE getCachedRecipe() {
        String key = contentHandler.getCacheKey();
        if (cachedRecipes.containsKey(key)) {
            if (cachedRecipes.get(key).test(contentHandler)) {
                return cachedRecipes.get(key);
            }
        }
        return null;
    }

    protected int getBaseProcessTime() {
        return prefab().config().getTime();
    }

    protected int getBasePower() {
        return prefab().config().getPower();
    }

    protected void handleRecipeOutput() {
        if (hasRecipe() && recipeInfo.isCompleted()) {
            if (recipe.handleOutputs(contentHandler)) {
                recipeInfo.clear();
            } else {
                recipeInfo.stuck = true;
            }
        }
    }

    public double speedMultiplier() {
        if (!prefab().supportSpeedUpgrade) return 1;
        int id = prefab().supportEnergyUpgrade ? 1 : 0;
        speedMultiplier = upgradesHandler.getStackInSlot(id).getCount() + 1;
        return speedMultiplier;
    }

    public int energyPerTick() {
        double energy = recipe == null ? prefab().config().getPower() : recipe.getEnergy();
        energyPerTick = (int) (energy * energyMultiplier() * prefab().config().getPower());
        return energyPerTick;
    }

    public boolean recipeIsStuck() {
        if (recipeInfo.isCompleted() || recipeInfo.recipe == null) {
            handleRecipeOutput();
        }
        return false;
    }

    public boolean hasRecipe() {
        return recipeInfo.recipe != null;
    }

    public int getEnergyCapacity() {
        return prefab().config().getPower() * 5000;
    }

    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(getEnergyCapacity(), 100000, 0) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    protected CatalystHandler createCatalystHandler() {
        return new CatalystHandler(this);
    }

    protected UpgradesHandler createUpgradesHandler() {
        return new UpgradesHandler(this);
    }

    protected boolean gtEUSupported() {
        return PROCESSOR_CONFIG.GT_SUPPORT.get() == 2 || PROCESSOR_CONFIG.GT_SUPPORT.get() == 1;
    }

//    @Nonnull TODO implement
//    @Override
//    public <T> Supplier<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            return contentHandler.getItemCapability(side);
//        }
//        if (cap == ForgeCapabilities.FLUID_HANDLER) {
//            return contentHandler.getFluidCapability(side);
//        }
//        if (cap == ForgeCapabilities.ENERGY && PROCESSOR_CONFIG.GT_SUPPORT.get() != 2) {
//            if (prefab().config().getPower() > 0) {
//                return energy.cast();
//            }
//            return LazyOptional.empty();
//        }
//
//        if (isGtLoaded() && gtEUSupported()) {
//            if (cap == com.gregtechceu.gtceu.api.capability.forge.GTCapability.CAPABILITY_ENERGY_CONTAINER) {
//                return getGTEnergyHandler(cap, side);
//            }
//        }
//
//        if (isCcLoaded()) {
//            if (cap == dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL) {
//                return getPeripheral(cap, side);
//            }
//        }
//
//        if (isOC2Loaded()) {
//            if (cap == DEVICE_CAPABILITY) {
//                return getOCDevice(cap, side);
//            }
//        }
//
//        if (isMekanismLoadeed()) {
//            if (cap == mekanism.common.capabilities.Capabilities.GAS_HANDLER) {
//                if (contentHandler.hasFluidCapability(side)) {
//                    return LazyOptional.of(() -> contentHandler.gasConverter(side));
//                }
//                return LazyOptional.empty();
//            }
//            if (cap == mekanism.common.capabilities.Capabilities.SLURRY_HANDLER) {
//                if (contentHandler.hasFluidCapability(side)) {
//                    return LazyOptional.of(() -> contentHandler.getSlurryConverter(side));
//                }
//                return LazyOptional.empty();
//            }
//        }
//        return super.getCapability(cap, side);
//    }

    private <T> Supplier<T> getOCDevice(DeferredRegister<T> cap, Direction side) {
        return () -> (T) NCProcessorDevice.createDevice(this);
    }

    protected <T> Supplier<NCGTEnergyHandler> getGTEnergyHandler(DeferredRegister<T> cap, Direction side) {
        if (gtEnergyCap == null) {
            NCGTEnergyHandler handler = new NCGTEnergyHandler(energyStorage, PROCESSOR_CONFIG.BASE_POWER.get() / 4, PROCESSOR_CONFIG.GT_AMPERAGE.get());
            gtEnergyCap = () -> handler;
        }
        return gtEnergyCap;
    }

    public NCProcessorBE(BlockPos pPos, BlockState pBlockState, String name) {
        super(NCProcessors.PROCESSORS_BE.get(name).get(), pPos, pBlockState);
        prefab = Processors.all().get(name);
        contentHandler = new SidedContentHandler(
                prefab().getSlotsConfig().getInputItems(), prefab().getSlotsConfig().getOutputItems(),
                prefab().getSlotsConfig().getInputFluids(), prefab().getSlotsConfig().getOutputFluids());
        contentHandler.setBlockEntity(this);
        energyStorage = createEnergy();
        energy = () -> energyStorage;
    }

    public void tickClient() {
        if (isActive && level.getRandom().nextInt(50) < 5) {
            BlockPos pos = worldPosition;
            Direction direction = getFacing();
            Direction.Axis direction$axis = direction.getAxis();
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY();
            double d2 = (double) pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = level.getRandom().nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
            double d6 = level.getRandom().nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
            level.addParticle(particle1, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0, 0.0D);
            level.addParticle(DustParticleOptions.REDSTONE, d0 + d5, d1 + d6, d2 + d7, 0, 0, 0);
        }
    }

    public List<ItemStack> getAllowedInputItems() {
        if (allowedInputs == null) {
            allowedInputs = new ArrayList<>();
            for (AbstractRecipe recipe : NcRecipeType.ALL_RECIPES.get(getName()).getRecipeType().getRecipes(getLevel())) {
                for (Ingredient ingredient : recipe.getItemIngredients()) {
                    allowedInputs.addAll(List.of(ingredient.getItems()));
                }
            }
        }
        return allowedInputs;
    }

    protected int howMuchICanSkip() {
        if (energyPerTick() == 0) {
            return PROCESSOR_CONFIG.SKIP_TICKS.get();
        }
        return Math.min(((int) (energyStorage.getEnergyStored() / energyPerTick())), PROCESSOR_CONFIG.SKIP_TICKS.get());
    }

    public void tickServer() {
        if (NuclearCraft.instance.isNcBeStopped || isRemoved()) return;
        if (redstoneMode == 1 && !hasRedstoneSignal()) return;
        if (howMuchICanSkip() >= skippedTicks) {
            skippedTicks++;
            return;
        }
        boolean updated = manualUpdate();
        contentHandler.setAllowedInputItems(this::getAllowedInputItems);
        for (int i = 0; i < prefab().getSlotsConfig().getInputFluids(); i++) {
            contentHandler.setAllowedInputFluids(i, this::getAllowedInputFluids);
        }
        processRecipe();
        handleRecipeOutput();
        updated = updated || contentHandler.tick();
        if (updated || wasUpdated) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ACTIVE, isActive));
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState().setValue(ACTIVE, isActive), Block.UPDATE_ALL);
        }
        skippedTicks = 1;

    }

    public List<FluidStack> getAllowedInputFluids() {
        if (allowedFluids == null) {
            allowedFluids = new ArrayList<>();
            for (NcRecipe recipe : NcRecipeType.ALL_RECIPES.get(getName()).getRecipeType().getRecipes(getLevel())) {
                for (FluidStackIngredient ingredient : recipe.getInputFluids()) {
                    allowedFluids.addAll(ingredient.getRepresentations());
                }
            }
        }
        return allowedFluids;
    }

    private boolean manualUpdate() {
        if (manualUpdateCounter > 0) {
            manualUpdateCounter--;
            return false;
        }
        manualUpdateCounter = 40;
        saveSideMapFlag = true;
        energyStorage.wasUpdated = true;
        upgradesHandler.wasUpdated = true;
        catalystHandler.wasUpdated = true;
        return true;
    }

    public boolean hasRedstoneSignal() {
        return Objects.requireNonNull(getLevel()).hasNeighborSignal(worldPosition);
    }


    protected void processRecipe() {
        if (!hasRecipe()) {
            updateRecipe();
        }
        if (!hasRecipe()) {
            isActive = false;
            return;
        }

        if (energyStorage.getEnergyStored() < energyPerTick() * skippedTicks) {
            isActive = false;
            return;
        }
        if (!canProcessRecipe()) {
            return;
        }
        recipeInfo.process(speedMultiplier() * skippedTicks);
        if (recipeInfo.radiation != 1D) {
            RadiationManager.get(getLevel()).addRadiation(getLevel(), (recipeInfo.radiation / 1000000) * speedMultiplier() * skippedTicks, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        }
        isActive = true;
        setChanged();
        if (!recipeInfo.isCompleted() && hasRecipe()) {
            energyStorage.consumeEnergy(energyPerTick() * skippedTicks);
        }
    }

    protected boolean canProcessRecipe() {
        return true;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        wasUpdated = true;
    }

    public int getEnergyUpgrades() {
        if (!prefab().supportEnergyUpgrade) return 1;
        return upgradesHandler.getStackInSlot(0).getCount() + 1;
    }

    public int energyMultiplier() {
        energyMultiplier = (int) Math.max(speedMultiplier(), Math.pow(speedMultiplier() - 1, 2) + speedMultiplier() - Math.pow(getEnergyUpgrades(), 2));
        return energyMultiplier;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        invalidateCapabilities();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(registries, tag.get("Energy"));
        }
        if (tag.contains("Content")) {
            contentHandler.deserializeNBT(registries, tag.getCompound("Content"));
        }
        if (tag.contains("Info")) {
            CompoundTag infoTag = tag.getCompound("Info");
            readTagData(infoTag);
            if (infoTag.contains("recipeInfo")) {
                recipeInfo.deserializeNBT(registries, infoTag.getCompound("recipeInfo"));
            }
            if (infoTag.contains("upgrades")) {
                upgradesHandler.deserializeNBT(registries, (CompoundTag) (infoTag).get("upgrades"));
            }
            if (infoTag.contains("catalyst")) {
                catalystHandler.deserializeNBT(registries, (CompoundTag) (infoTag).get("catalyst"));
            }
        }

        if (tag.contains("playerUID")) {
            playerUID = tag.getUUID("playerUID");
        }
        updateRecipeAfterLoad();
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        contentHandler.saveSideMap();

        if (!tag.contains("Content")) {
            tag.put("Content", contentHandler.serializeNBT(registries));
        }
        if (!tag.contains("Energy")) {
            tag.put("Energy", energyStorage.serializeNBT(registries));
        }
        CompoundTag infoTag = new CompoundTag();
        saveTagData(infoTag);
        infoTag.put("upgrades", upgradesHandler.serializeNBT(registries));
        infoTag.put("catalyst", catalystHandler.serializeNBT(registries));
        infoTag.put("recipeInfo", recipeInfo.serializeNBT(registries));
        tag.put("Info", infoTag);
        if (playerUID != null) {
            tag.putUUID("playerUID", playerUID);
        }
    }

    @Override
    public void loadClientData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (tag.contains("Info")) {
            CompoundTag infoTag = tag.getCompound("Info");
            readTagData(infoTag);
            if (infoTag.contains("recipeInfo")) {
                recipeInfo.deserializeNBT(lookupProvider, infoTag.getCompound("recipeInfo"));
            }
            if (infoTag.contains("energy")) {
                energyStorage.setEnergy(infoTag.getInt("energy"));
            }
            if (infoTag.contains("upgrades")) {
                upgradesHandler.deserializeNBT(lookupProvider, (CompoundTag) (infoTag).get("upgrades"));
            }
            if (infoTag.contains("catalyst")) {
                catalystHandler.deserializeNBT(lookupProvider, (CompoundTag) (infoTag).get("catalyst"));
            }
        }
        if (tag.contains("Content")) {
            contentHandler.deserializeNBT(lookupProvider, tag.getCompound("Content"));
        }
    }

    @Override
    protected void saveClientData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag infoTag = new CompoundTag();
        saveTagData(infoTag);
        if (saveSideMapFlag) {
            contentHandler.saveSideMap();
            saveSideMapFlag = false;
        }
        if (upgradesHandler.wasUpdated) {
            infoTag.put("upgrades", upgradesHandler.serializeNBT(registries));
            upgradesHandler.wasUpdated = false;
        }
        if (catalystHandler.wasUpdated) {
            infoTag.put("catalyst", catalystHandler.serializeNBT(registries));
            catalystHandler.wasUpdated = false;
        }
        infoTag.put("recipeInfo", recipeInfo.serializeNBT(registries));
        tag.put("Info", infoTag);
        tag.put("Content", contentHandler.serializeNBT(registries));
        infoTag.putInt("energy", energyStorage.getEnergyStored());
    }

    private void updateRecipeAfterLoad() {
        if (recipe == null && recipeInfo != null && recipeInfo.recipe() != null) {
            recipe = recipeInfo.recipe();
        }
    }

    public double getProgress() {
        return recipeInfo.getProgress();
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

    public int toggleSideConfig(int slotId, int direction) {
        setChanged();
        saveSideMapFlag = true;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return contentHandler.toggleSideConfig(slotId, direction);
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public SlotModePair.SlotMode getSlotMode(int direction, int slotId) {
        return contentHandler.getSlotMode(direction, slotId);
    }

    public FluidTank getFluidTank(int i) {
        return contentHandler.fluidCapability.tanks.get(i);
    }

    public void toggleRedstoneMode() {
        redstoneMode++;
        if (redstoneMode > 1) redstoneMode = 0;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public CompoundTag getTagForStack(HolderLookup.Provider lookupProvider) {
        CompoundTag data = new CompoundTag();
        contentHandler.saveSideMap();
        data.put("Content", contentHandler.serializeNBT(lookupProvider));
        data.put("Energy", energyStorage.serializeNBT(lookupProvider));
        CompoundTag infoTag = new CompoundTag();
        saveTagData(infoTag);
        infoTag.put("upgrades", upgradesHandler.serializeNBT(lookupProvider));
        infoTag.put("catalyst", catalystHandler.serializeNBT(lookupProvider));
        infoTag.put("recipeInfo", recipeInfo.serializeNBT(lookupProvider));
        infoTag.putInt("energy", energyStorage.getEnergyStored());
        data.put("Info", infoTag);
        return data;
    }

    public List<Item> getAllowedCatalysts() {
        return List.of();
    }

    public int getRecipeProgress() {
        if (hasRecipe()) {
            return (int) (recipeInfo.getProgress() * 100);
        }
        return 0;
    }

    public int getSlotsCount() {
        return prefab().getSlotsConfig().slotsCount();
    }

    public void voidSlotContent(int id) {
        if (id < 0 || id >= getSlotsCount()) return;
        contentHandler.voidSlot(id);
    }

    public Object[] getSlotContent(int id) {
        if (id < 0 || id >= getSlotsCount()) return new Object[]{};
        return contentHandler.getSlotContent(id);
    }

    public void voidFluidSlot(int slotId) {
        if (contentHandler != null) {
            contentHandler.voidFluidSlot(slotId);
        }
    }

    public boolean isInputAllowed(ItemStack stack) {
        for (ItemStack allowed : getAllowedInputItems()) {
            if (ItemStack.isSameItem(allowed, stack)) {
                return true;
            }
        }
        return false;
    }

    public List<Item> getAllowedItems(int idx) {
        if (contentHandler.itemHandler.validItemsForSlot.containsKey(idx)) {
            return contentHandler.itemHandler.validItemsForSlot.get(idx);
        }
        List<Item> allowedItems = new ArrayList<>();
        for (ItemStack stack : getAllowedInputItems()) {
            allowedItems.add(stack.getItem());
        }
        return allowedItems;
    }
}
