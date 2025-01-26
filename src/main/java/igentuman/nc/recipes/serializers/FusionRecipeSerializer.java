package igentuman.nc.recipes.serializers;

import igentuman.nc.NuclearCraft;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.type.NcRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FusionRecipeSerializer<RECIPE extends NcRecipe> extends NcRecipeSerializer<RECIPE> {

    public FusionRecipeSerializer(IFactory factory) {
        super(factory);
    }

//    @Override
//    public RECIPE fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
//        try {
//            ItemStackIngredient[] inputItems = readItems(buffer);
//            ItemStackIngredient[] outputItems = readItems(buffer);
//            FluidStackIngredient[] inputFluids = readFluids(buffer);
//            FluidStackIngredient[] outputFluids = readFluids(buffer);
//
//            double timeModifier = buffer.readDouble();
//            double powerModifier = buffer.readDouble();
//            double radiation = buffer.readDouble();
//            double temperature = buffer.readDouble();
//
//            return this.factory.create(recipeId, inputItems, outputItems, inputFluids,  outputFluids, timeModifier, powerModifier, radiation, temperature);
//        } catch (Exception e) {
//            NuclearCraft.LOGGER.error("Error reading from packet.", e);
//            throw e;
//        }
//    }
}
