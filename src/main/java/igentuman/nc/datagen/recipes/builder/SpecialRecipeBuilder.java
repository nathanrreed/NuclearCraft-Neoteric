package igentuman.nc.datagen.recipes.builder;

import com.google.gson.JsonObject;
import igentuman.nc.registry.RecipeSerializerRegistryObject;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class SpecialRecipeBuilder {

    private final RecipeSerializer<?> serializer;

    private SpecialRecipeBuilder(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static void build(RecipeOutput consumer, RecipeSerializerRegistryObject<?> serializer) {
        build(consumer, serializer.get());
    }

    public static void build(RecipeOutput consumer, RecipeSerializer<?> serializer) {
//        consumer.accept(new SpecialRecipeBuilder(serializer));
    }

//    @Override
//    public RecipeSerializer<?> getType() {
//        return serializer;
//    }
//
//    @Override
//    public void serializeRecipeData(JsonObject json) {
//        //NO-OP
//    }

    private static <T> ResourceLocation getName(Registry<T> registry, T element) {
        return registry.getKey(element);
    }

    public static ResourceLocation getName(RecipeSerializer<?> element) {
        return getName(BuiltInRegistries.RECIPE_SERIALIZER, element);
    }

//    @Override
//    public ResourceLocation getId() {
//        return getName(getType());
//    }
//
//    @Nullable
//    @Override
//    public JsonObject serializeAdvancement() {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public ResourceLocation getAdvancementId() {
//        return null;
//    }
}