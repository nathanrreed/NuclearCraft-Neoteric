package igentuman.nc.datagen.recipes.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.NuclearCraft.MODID;

public abstract class RecipeBuilder<BUILDER extends RecipeBuilder<BUILDER>> implements net.minecraft.data.recipes.RecipeBuilder {

    protected static ResourceLocation ncSerializer(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    protected final List<ICondition> conditions = new ArrayList<>();
    protected final ResourceLocation serializerName;

    protected RecipeBuilder(ResourceLocation serializerName) {
        this.serializerName = serializerName;
    }

    /**
     * Performs any extra validation.
     *
     * @param id ID of the recipe validation is being performed on.
     */
    protected void validate(ResourceLocation id) {
    }

    /**
     * Builds this recipe.
     *
     * @param consumer Finished Recipe Consumer.
     * @param id       Name of the recipe being built.
     */
    public void build(RecipeOutput consumer, ResourceLocation id) {
        validate(id);
        save(consumer, id);
    }

    /**
     * Builds this recipe basing the name on the output item.
     *
     * @param consumer Finished Recipe Consumer.
     * @param output   Output to base the recipe name off of.
     */
    protected void build(RecipeOutput consumer, ItemLike... output) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(output[0].asItem());
        if (registryName == BuiltInRegistries.ITEM.getDefaultKey()) {
            throw new IllegalStateException("Could not retrieve registry name for output.");
        }
        build(consumer, registryName);
    }
}