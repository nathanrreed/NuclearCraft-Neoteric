
package igentuman.nc.datagen.recipes.recipes;

import igentuman.nc.block.entity.fission.FissionControllerBE;
import igentuman.nc.datagen.recipes.builder.NcRecipeBuilder;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.ItemStackIngredientCreator;
import igentuman.nc.setup.registration.FissionFuel;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.NuclearCraft.rl;

public class FissionRecipes {

    public static RecipeOutput consumer;

    public static void generate(RecipeOutput consumer) {
        FissionRecipes.consumer = consumer;
        solidFissionRecipes();
    }

    private static void itemToItemRecipe(String id, ItemStackIngredient input, Item output, double... params) {
        itemToItemRecipe(id, input, ItemStackIngredientCreator.INSTANCE.from(output), params);
    }

    private static void itemToItemRecipe(String id, ItemStackIngredient input, ItemStackIngredient output, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(id)
                .items(List.of(input), List.of(output))
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer, rl(id + "/" + output.getRepresentations().stream().findFirst().orElseGet(() -> ItemStack.EMPTY).getDescriptionId().replace("depleted_fuel_", "")));
    }

    private static void solidFissionRecipes() {
        for (List<String> name : FissionFuel.NC_FUEL.keySet()) {
            if (name.contains("tr")) continue;
            List<String> depleted = new ArrayList<>(name);
            depleted.set(0, "depleted");
            itemToItemRecipe(FissionControllerBE.NAME,
                    ItemStackIngredientCreator.INSTANCE.from(FissionFuel.NC_FUEL.get(name).get()),
                    FissionFuel.NC_DEPLETED_FUEL.get(depleted).get());
        }
    }
}
