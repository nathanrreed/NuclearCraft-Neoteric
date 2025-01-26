package igentuman.nc.datagen.recipes.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import igentuman.nc.datagen.recipes.NCRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.NuclearCraft.MODID;

public abstract class RecipeBuilder<BUILDER extends RecipeBuilder<BUILDER>> {

    protected static ResourceLocation ncSerializer(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    protected final List<ICondition> conditions = new ArrayList<>();
    protected final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    protected final ResourceLocation serializerName;

    protected RecipeBuilder(ResourceLocation serializerName) {
        this.serializerName = serializerName;
    }

//    /**
//     * Adds a criterion to this recipe.
//     *
//     * @param criterion Criterion to add.
//     */
//    public BUILDER addCriterion(RecipeCriterion criterion) {
//        return addCriterion(criterion.name(), criterion.criterion());
//    }

    /**
     * Adds a criterion to this recipe.
     *
     * @param name      Name of the criterion.
     * @param criterion Criterion to add.
     */
    public BUILDER addCriterion(String name, Criterion<RecipeUnlockedTrigger.TriggerInstance> criterion) {
        advancementBuilder.addCriterion(name, criterion);
        return (BUILDER) this;
    }

    /**
     * Adds a condition to this recipe.
     *
     * @param condition Condition to add.
     */
    public BUILDER addCondition(ICondition condition) {
        conditions.add(condition);
        return (BUILDER) this;
    }

//    /**
//     * Checks if this recipe has any criteria.
//     *
//     * @return {@code true} if this recipe has any criteria.
//     */
//    protected boolean hasCriteria() {
//        return !advancementBuilder.getCriteria().isEmpty();
//    }

    /**
     * Gets a recipe result object.
     *
     * @param id ID of the recipe being built.
     */
    protected abstract RecipeResult getResult(ResourceLocation id);

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
//        if (hasCriteria()) {
        //If there is a way to "unlock" this recipe then add an advancement with the criteria
        advancementBuilder.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
//        }

//        NCRecipe recipe = NC(this.inputItem, this.result, this.power, this.processingTime); //TODO
//        consumer.accept(id, recipe, build(id.withPrefix("recipes/");
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

    /**
     * Base recipe result.
     */
    protected abstract class RecipeResult implements RecipeOutput {

        private final ResourceLocation id;

        public RecipeResult(ResourceLocation id) {
            this.id = id;
        }

//        @Override
//        public JsonObject serializeRecipe() {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("type", serializerName.toString());
//            if (!conditions.isEmpty()) {
//                JsonArray conditionsArray = new JsonArray();
//                for (ICondition condition : conditions) {
//                    conditionsArray.add(CraftingHelper.serialize(condition));
//                }
//                jsonObject.add("conditions", conditionsArray);
//            }
//            this.serializeRecipeData(jsonObject);
//            return jsonObject;
//        }

//        @NotNull
//        @Override
//        public RecipeSerializer<?> getType() {
//            return BuiltInRegistries.RECIPE_SERIALIZER.get(serializerName);
//        }
//
//        @NotNull
//        @Override
//        public ResourceLocation getId() {
//            return this.id;
//        }

//        @Nullable
//        @Override
//        public JsonObject serializeAdvancement() {
//            return hasCriteria() ? advancementBuilder.serializeToJson() : null;
//        }

//        @Nullable
//        @Override
//        public ResourceLocation getAdvancementId() {
//            return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + id.getPath());
//        }
    }

    public static JsonElement serializeItemStack(@NotNull ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        if (stack.getCount() > 1) {
            json.addProperty("count", stack.getCount());
        }

        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> {
            if (compoundTag.contains("Damage")) {
                if (compoundTag.getInt("Damage") == 0) {
                    compoundTag.remove("Damage");
                }
            }
            if (!compoundTag.getAllKeys().isEmpty())
                json.addProperty("nbt", compoundTag.toString());
        }));
        return json;
    }

//    public static JsonElement serializeIngredient(@NotNull Ingredient ingredient) {
//        return ingredient.toJson();
//    }


//    public static JsonElement serializeFluidStack(@NotNull FluidStack fluidStack) {
//        JsonObject json = new JsonObject();
//        json.addProperty("fluid", BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString());
//        json.addProperty("amount", fluidStack.getAmount());
//        if (fluidStack.hasTag()) {
//            if (fluidStack.getTag().contains("Damage")) {
//                if (fluidStack.getTag().getInt("Damage") == 0) {
//                    fluidStack.getTag().remove("Damage");
//                }
//            }
//            if (!fluidStack.getTag().getAllKeys().isEmpty())
//                json.addProperty("nbt", fluidStack.getTag().toString());
//        }
//        return json;
//    }
}