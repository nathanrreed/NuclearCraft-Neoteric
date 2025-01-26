package igentuman.nc.registry;

import igentuman.nc.recipes.INcRecipeTypeProvider;
import igentuman.nc.recipes.NcRecipeType;
import igentuman.nc.recipes.type.NcRecipe;

import java.util.function.Supplier;

public class RecipeTypeRegistryObject<RECIPE extends NcRecipe> extends
        WrappedRegistryObject<NcRecipeType<RECIPE>> implements INcRecipeTypeProvider<RECIPE> {

    public RecipeTypeRegistryObject(Supplier<NcRecipeType<RECIPE>> registryObject) {
        super(registryObject);
    }

    @Override
    public NcRecipeType<RECIPE> getRecipeType() {
        return get();
    }
}