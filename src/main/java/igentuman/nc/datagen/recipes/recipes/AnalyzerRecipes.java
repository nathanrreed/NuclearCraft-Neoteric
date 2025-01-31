package igentuman.nc.datagen.recipes.recipes;

import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.creator.ItemStackIngredientCreator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;

import static igentuman.nc.setup.registration.NCItems.NC_PARTS;
import static net.minecraft.world.item.Items.FILLED_MAP;
import static net.minecraft.world.item.Items.PAPER;

public class AnalyzerRecipes extends AbstractRecipeProvider {

    public static void generate(RecipeOutput consumer) {
        AnalyzerRecipes.consumer = consumer;
        ID = Processors.ANALYZER;

        ItemStack dataPaper = new ItemStack(NC_PARTS.get("research_paper").get());
        dataPaper.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putBoolean("vein_data", true)));
        itemToItem(ingredient(PAPER), ItemStackIngredientCreator.INSTANCE.from(dataPaper), 2.5D, 4D);

        ItemStack dataMap = new ItemStack(FILLED_MAP);
        dataPaper.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putBoolean("is_nc_analyzed", true)));
        itemToItem(ingredient(FILLED_MAP), ItemStackIngredientCreator.INSTANCE.from(dataMap), 5.5D, 10D);
    }
}
