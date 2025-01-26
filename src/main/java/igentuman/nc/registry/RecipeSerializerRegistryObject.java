package igentuman.nc.registry;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class RecipeSerializerRegistryObject<RECIPE extends Recipe<?>> extends WrappedRegistryObject<RecipeSerializer<RECIPE>> {
    public RecipeSerializerRegistryObject(Supplier<RecipeSerializer<RECIPE>> registryObject) {
        super(registryObject);
    }
}