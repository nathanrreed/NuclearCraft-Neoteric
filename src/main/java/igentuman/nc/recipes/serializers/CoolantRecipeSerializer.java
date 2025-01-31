package igentuman.nc.recipes.serializers;

import igentuman.nc.recipes.type.NcRecipe;

public class CoolantRecipeSerializer<RECIPE extends NcRecipe> extends NcRecipeSerializer<RECIPE> {

    public CoolantRecipeSerializer(IFactory factory) {
        super(factory);
    }
}
