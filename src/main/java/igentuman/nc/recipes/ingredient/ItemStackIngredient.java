package igentuman.nc.recipes.ingredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ItemStackIngredient implements InputIngredient<@NotNull ItemStack> {
    public abstract int getAmount();

    public abstract List<Ingredient> getInputsRaw();
}