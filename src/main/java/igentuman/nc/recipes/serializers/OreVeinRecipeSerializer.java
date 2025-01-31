package igentuman.nc.recipes.serializers;

import igentuman.nc.recipes.type.NcRecipe;

public class OreVeinRecipeSerializer<RECIPE extends NcRecipe> extends NcRecipeSerializer<RECIPE> {

    public OreVeinRecipeSerializer(IFactory factory) {
        super(factory);
    }
}
