package igentuman.nc.datagen.recipes.recipes;

import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.List;

public class ElectrolyzerRecipes extends AbstractRecipeProvider {

    public static void generate(RecipeOutput consumer) {
        ElectrolyzerRecipes.consumer = consumer;
        ID = Processors.ELECTROLYZER;

        add(fluidIngredient("minecraft:water", 500),
                List.of(
                        fluidIngredient("hydrogen", 500),
                        fluidIngredient("oxygen", 250)
                ), 0.5D
        );
        add(fluidIngredient("heavy_water", 500),
                List.of(
                        fluidIngredient("deuterium", 500),
                        fluidIngredient("oxygen", 250)
                ), 0.5D
        );
        add(fluidIngredient("hydrofluoric_acid", 250),
                List.of(
                        fluidIngredient("hydrogen", 250),
                        fluidIngredient("fluorine", 250)
                ), 0.5D
        );
    }

    protected static void add(FluidStackIngredient input, List<FluidStackIngredient> output, double... modifiers) {
        fluidsAndFluids(List.of(input), output, modifiers);
    }
}
