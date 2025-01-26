package igentuman.nc.recipes.ingredient;

import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public abstract class FluidStackIngredient implements InputIngredient<@NotNull FluidStack> {
    protected int amount;

    public int getAmount() {
        return amount;
    }
}