package igentuman.nc.handler.sided.capability;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

public class Chemical2FluidConverter implements IChemicalHandler {

    private FluidCapabilityHandler fluidCapability;
    private Direction side;

    @Override
    public int getChemicalTanks() {
        return 1;
    }

    @Override
    public @NotNull ChemicalStack getChemicalInTank(int tank) {
        return ChemicalStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int tank, @NotNull ChemicalStack stack) {

    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        return 100;
    }

    @Override
    public boolean isValid(int tank, @NotNull ChemicalStack stack) {
        return false;
    }

    private String specialConvertRules(String input) {
        if (input.matches("clean_[a-z]+")) {
            return input.substring(6) + "_clean_slurry";
        }
        if (input.matches("dirty_[a-z]+")) {
            return input.substring(6) + "_slurry";
        }
        return input;
    }

    private HashMap<Chemical, Fluid> gasFluidMap = new HashMap<>();

    private FluidStack convert(ChemicalStack stack) {
        int amount = (int) stack.getAmount();
        if (amount <= 0) amount = 1000;
        if (gasFluidMap.containsKey(stack.getChemical())) {
            if (!(gasFluidMap.get(stack.getChemical()) instanceof EmptyFluid)) {
                return new FluidStack(gasFluidMap.get(stack.getChemical()), amount);
            }
        }
        String name = stack.getTypeRegistryName().getPath();
        name = specialConvertRules(name);

        TagKey<Fluid> key = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("neoforge", name));
        FluidStack fluidStack;
        try {
            fluidStack = Arrays.stream(FluidIngredient.tag(key).getStacks()).toList().getFirst().copyWithAmount(amount);
        } catch (Exception e) {
            return FluidStack.EMPTY;
        }

        gasFluidMap.put(stack.getChemical(), fluidStack.getFluid());
        return new FluidStack(gasFluidMap.get(stack.getChemical()), (int) stack.getAmount());
    }

    @Override
    public @NotNull ChemicalStack insertChemical(int tank, @NotNull ChemicalStack stack, @NotNull Action action) {
        FluidStack fluidStack = convert(stack);
        if (fluidStack.isEmpty()) return stack;
        for (int i = 0; i < fluidCapability.inputSlots; i++) {
            if (!fluidCapability.haveAccessFromSide(side, i)) continue;
            if (fluidCapability.isValidForInputSlot(i, fluidStack)) {
                boolean doInsert = action.execute();
                FluidStack inserted = fluidCapability.insertFluidInternal(i, fluidStack, doInsert);
                stack.setAmount(inserted.getAmount());
                return stack;
            }
        }
        return stack;
    }

    @Override
    public @NotNull ChemicalStack extractChemical(int tank, long amount, @NotNull Action action) {
        return ChemicalStack.EMPTY;
    }

    public void setFluidHandler(FluidCapabilityHandler fluidCapability) {
        this.fluidCapability = fluidCapability;
    }

    public Chemical2FluidConverter forSide(Direction side) {
        this.side = side;
        return this;
    }
}
