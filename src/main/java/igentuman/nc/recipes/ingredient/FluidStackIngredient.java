package igentuman.nc.recipes.ingredient;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class FluidStackIngredient implements InputIngredient<@NotNull FluidStack> {
    protected int amount;

    public int getAmount() {
        return amount;
    }

    public abstract List<FluidStack> getInputsRaw();
}