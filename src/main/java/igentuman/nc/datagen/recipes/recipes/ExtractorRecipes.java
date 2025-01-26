package igentuman.nc.datagen.recipes.recipes;

import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.setup.registration.NCItems.ALL_NC_ITEMS;

public class ExtractorRecipes extends AbstractRecipeProvider {

    public static void generate(RecipeOutput consumer) {
        ExtractorRecipes.consumer = consumer;
        ID = Processors.EXTRACTOR;
        add(ingredient(ALL_NC_ITEMS.get("ground_cocoa_nibs").get()), ingredient(ALL_NC_ITEMS.get("cocoa_solids").get()), fluidIngredient("cocoa_butter", 144));

    }

    protected static void add(Ingredient inputItem, Ingredient outputItem, FluidStackIngredient outputFluid, double... modifiers) {
        itemsAndFluids(List.of(inputItem), List.of(outputItem), new ArrayList<>(), List.of(outputFluid), modifiers);
    }
}
