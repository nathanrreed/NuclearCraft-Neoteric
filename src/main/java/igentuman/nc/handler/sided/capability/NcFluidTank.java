package igentuman.nc.handler.sided.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class NcFluidTank extends FluidTank {

    public NcFluidTank(int capacity) {
        super(capacity);
    }

    @Override
    public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        FluidStack fluid = FluidStack.parseOptional(lookupProvider, nbt);
        setFluid(fluid);
        capacity = nbt.getInt("Capacity");
        return this;
    }

    @Override
    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        fluid.save(lookupProvider, nbt);
        nbt.putInt("Capacity", capacity);
        return nbt;
    }
}
