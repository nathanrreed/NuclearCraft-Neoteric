package igentuman.nc.recipes.serializers;

import igentuman.nc.recipes.type.NcRecipe;

public class OreVeinRecipeSerializer<RECIPE extends NcRecipe> extends NcRecipeSerializer<RECIPE> {

    public OreVeinRecipeSerializer(IFactory factory) {
        super(factory);
    }

//    @Override TODO
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
//            double rarity = buffer.readDouble();
//
//            return this.factory.create(recipeId, inputItems, outputItems, inputFluids, outputFluids, timeModifier, powerModifier, radiation, rarity);
//        } catch (Exception e) {
//            NuclearCraft.LOGGER.error("Error reading from packet.", e);
//            throw e;
//        }
//    }
}
