package igentuman.nc.handler.sided.capability;

import igentuman.nc.handler.sided.SidedContentHandler;
import igentuman.nc.handler.sided.SlotModePair;
import igentuman.nc.handler.sided.SlotModePair.SlotMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.function.Supplier;

import static igentuman.nc.handler.sided.SlotModePair.SlotMode.*;

public class FluidCapabilityHandler extends AbstractCapabilityHandler implements INBTSerializable<CompoundTag> {
    public final NonNullList<NcFluidTank> tanks;
    public final NonNullList<Supplier<IFluidHandler>> fluidCapabilites;

    protected FluidStack[] sortedFluids;
    public List<FluidStack> holdedInputs = new ArrayList<>();
    private Map<Direction, Supplier<FluidHandlerWrapper>> handlerCache = new HashMap<>();

    public HashMap<Integer, Supplier<List<FluidStack>>> allowedFluids;

    public FluidCapabilityHandler(int inputSlots, int outputSlots, int inputCapacity, int outputCapacity) {
        tanks = NonNullList.create();
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        fluidCapabilites = NonNullList.create();
        for (int i = 0; i < inputSlots; i++) {
            int finalI = i;
            tanks.add(new NcFluidTank(inputCapacity * 1000));
            fluidCapabilites.add(() -> tanks.get(finalI));
        }
        for (int i = inputSlots; i < inputSlots + outputSlots; i++) {
            int finalI = i;
            tanks.add(new NcFluidTank(outputCapacity * 1000));
            fluidCapabilites.add(() -> tanks.get(finalI));
        }
        initDefault();
    }

    public Supplier<FluidHandlerWrapper> getCapability(Direction side) {
        if (side == null) return getCapability();

        if (!handlerCache.containsKey(side)) {
            SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(side, getFacing());
            handlerCache.put(side, () -> new FluidHandlerWrapper(this, relativeDirection, (i, f) -> inputAllowed(i, f, side), (i) -> outputAllowed(i, side)));
        }
        return handlerCache.get(side);
    }

    public boolean inputAllowed(Integer i, FluidStack fluid, Direction side) {
        if (side == null) return true;
        SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(side, getFacing());
        SlotMode mode = sideMap.get(relativeDirection.ordinal())[i].getMode();
        return (mode == INPUT || mode == PULL) && isValidSlotFluid(i, fluid) && isValidForInputSlot(i, fluid);
    }

    public boolean isValidSlotFluid(int id, FluidStack fluid) {
        if (allowedFluids == null) return true;
        if (!allowedFluids.containsKey(id)) return true;
        for (FluidStack stack : allowedFluids.get(id).get()) {
            if (FluidStack.isSameFluidSameComponents(stack, fluid)) {
                return true;
            }
        }
        return allowedFluids.isEmpty() || !allowedFluids.containsKey(id);
    }


    public boolean outputAllowed(Integer i, Direction side) {
        if (side == null) return true;
        SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(side, getFacing());
        SlotMode mode = sideMap.get(relativeDirection.ordinal())[i].getMode();
        return (mode == OUTPUT || mode == PUSH || mode == PUSH_EXCESS) && getFluidInSlot(i).getAmount() > 0;
    }

    public <T> Supplier<T> getCapability() {
        for (Direction side : Direction.values()) {
            for (SlotModePair slotModePair : sideMap.get(side.ordinal())) {
                if (slotModePair.getMode() != DISABLED)
                    return (Supplier<T>) fluidCapabilites.get(slotModePair.getSlot());
            }
        }
        return null;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < tanks.size(); i++) {
            tag.put("tank" + i, tanks.get(i).writeToNBT(provider, new CompoundTag()));
        }
        tag.putInt("size", tanks.size());
        if (sideMapUpdated) {
            sideMapUpdated = false;
            tag.put("sideMap", SidedContentHandler.serializeSideMap(sideMap));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        int size = compoundTag.getInt("size");
        for (int i = 0; i < size; i++) {
            tanks.get(i).readFromNBT(provider, compoundTag.getCompound("tank" + i));
        }
        if (!compoundTag.getCompound("sideMap").isEmpty()) {
            sideMap = SidedContentHandler.deserializeSideMap(compoundTag.getCompound("sideMap"));
        }
        onLoad();
    }


    public boolean pushFluids(Direction dir) {
        return pushFluids(dir, false, tile.getBlockPos());
    }

    public boolean pushFluids(Direction dir, boolean forceFlag, BlockPos pos) {
        IFluidHandler cap = tile.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if (cap != null) {
            SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(dir, getFacing());
            for (SlotModePair pair : sideMap.get(relativeDirection.ordinal())) {
                if (pair.getMode() == PUSH || forceFlag) {
                    NcFluidTank tank = tanks.get(pair.getSlot());
                    if (tank.getFluidAmount() > 0) {
                        int amount = cap.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
                        tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean pullFluids(Direction dir) {
        return pullFluids(dir, false, tile.getBlockPos());
    }

    public boolean pullFluids(Direction dir, boolean forceFlag, BlockPos pos) {
        IFluidHandler cap = tile.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(dir), dir.getOpposite());
        if (cap != null) {
            SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(dir, getFacing());
            for (SlotModePair pair : sideMap.get(relativeDirection.ordinal())) {
                if (pair.getMode() == PULL || forceFlag) {
                    NcFluidTank tank = tanks.get(pair.getSlot());
                    if (tank.getFluidAmount() < tank.getCapacity()) {
                        int amount = tank.fill(cap.drain(tank.getCapacity() - tank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        return amount > 0;
                    }
                }
            }
        }
        return false;
    }

    public FluidStack getFluidInSlot(int i) {
        return tanks.get(i).getFluid();
    }

    public String getCacheKey() {
        String key = "";
        if (sortedFluids == null) {
            sortedFluids = new FluidStack[inputSlots];
            for (int i = 0; i < inputSlots; i++) {
                sortedFluids[i] = getFluidInSlot(i);
            }
            Arrays.sort(sortedFluids, Comparator.comparing(fluidStack -> fluidStack.getFluid().toString()));
        }
        for (FluidStack tank : sortedFluids) {
            key += tank.getFluid().toString();
        }
        return key;
    }

    public boolean isValidForInputSlot(int i, FluidStack fluid) {
        if (outputAllowed(i, null)) {
            FluidStack stack = getFluidInSlot(i);
            if (stack.isEmpty()) return true;
            return FluidStack.isSameFluidSameComponents(stack, fluid);
        }
        return false;
    }

    public boolean isValidForOutputSlot(int i, FluidStack outputFluid) {
        if (outputAllowed(i, null)) {
            FluidStack stack = getFluidInSlot(i);
            if (stack.isEmpty()) return isValidSlotFluid(i, outputFluid);
            return FluidStack.isSameFluidSameComponents(stack, outputFluid);
        }
        return false;
    }

    public boolean canPushExcessFluid(int i, FluidStack outputFluid) {
        for (Direction dir : Direction.values()) {
            IFluidHandler cap = tile.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos().relative(dir), dir.getOpposite());
            if (cap != null) {
                SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(dir, getFacing());
                for (SlotModePair pair : sideMap.get(relativeDirection.ordinal())) {
                    if (pair.getSlot() != i) continue;
                    if (pair.getMode() == PUSH || pair.getMode() == PUSH_EXCESS) {
                        int amount = cap.fill(outputFluid, IFluidHandler.FluidAction.SIMULATE);
                        if (amount == outputFluid.getAmount()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public FluidStack insertFluidInternal(int i, FluidStack toOutput, boolean b) {
        FluidStack stack = getFluidInSlot(i);
        if (stack.isEmpty()) {
            if (!b) {
                tanks.get(i).fill(toOutput, IFluidHandler.FluidAction.EXECUTE);
            }
            return FluidStack.EMPTY;
        }
        if (FluidStack.isSameFluidSameComponents(stack, toOutput)) {
            int amount = tanks.get(i).fill(toOutput, IFluidHandler.FluidAction.SIMULATE);
            if (amount == toOutput.getAmount()) {
                if (!b) {
                    tanks.get(i).fill(toOutput, IFluidHandler.FluidAction.EXECUTE);
                }
                return FluidStack.EMPTY;
            }
            FluidStack result = toOutput.copy();
            result.shrink(amount);
            return result;
        }
        return toOutput;
    }

    public FluidStack pushExcessFluid(int i, FluidStack toOutput) {
        for (Direction dir : Direction.values()) {
            IFluidHandler cap = tile.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos().relative(dir), dir.getOpposite());
            if (cap != null) {
                SidedContentHandler.RelativeDirection relativeDirection = SidedContentHandler.RelativeDirection.toRelative(dir, getFacing());
                for (SlotModePair pair : sideMap.get(relativeDirection.ordinal())) {
                    if (pair.getMode() == PUSH_EXCESS) {
                        NcFluidTank tank = tanks.get(pair.getSlot());
                        if (tank.getFluidAmount() > 0 && toOutput.getFluid().equals(tank.getFluid().getFluid())) {
                            if (cap.fill(toOutput, IFluidHandler.FluidAction.SIMULATE) == toOutput.getAmount()) {
                                int amount = cap.fill(toOutput, IFluidHandler.FluidAction.EXECUTE);
                                //tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                                return FluidStack.EMPTY;
                            }
                        }
                    }
                }
            }
            return toOutput;
        }
        return toOutput;
    }

    public void voidSlot(int slotId) {
        tanks.get(slotId).setFluid(FluidStack.EMPTY);
    }

    public Object[] getSlotContent(int slotIdFromGlobalId) {
        FluidStack stack = tanks.get(slotIdFromGlobalId).getFluid();
        if (stack.isEmpty()) return new Object[]{};
        return new Object[]{BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString(), stack.getAmount()};
    }

    public boolean canPush() {
        for (int i = inputSlots; i < getSlots(); i++) {
            if (getFluidInSlot(i).getAmount() > 0) {
                return true;
            }
        }
        return false;
    }

    private int getSlots() {
        return tanks.size();
    }

    public boolean canPull() {
        for (int i = 0; i < inputSlots; i++) {
            if (getFluidInSlot(i).getAmount() < tanks.get(i).getCapacity()) {
                return true;
            }
        }
        return false;
    }
}