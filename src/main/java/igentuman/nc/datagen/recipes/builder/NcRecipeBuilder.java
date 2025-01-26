package igentuman.nc.datagen.recipes.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static igentuman.nc.NuclearCraft.MODID;

public class NcRecipeBuilder extends RecipeBuilder<NcRecipeBuilder> {

    private List<Ingredient> inputItems = List.of();
    private List<Ingredient> outputItems = List.of();
    private List<FluidStackIngredient> inputFluids = List.of();
    private List<FluidStackIngredient> outputFluids = List.of();
    private static NcRecipeBuilder instance;
    private double timeModifier = 1D;
    private double radiation = 1D;
    private double powerModifier = 1D;

    public double coolingRate = 0;
    public double heatRequired = 0;

    public String ID;
    private double rarityModifier = 1D;
    private double temperature = 0D;
    private List<String> outputItemsText = List.of();

    protected NcRecipeBuilder(String id) {
        super(ncSerializer(id));
        ID = id;
    }

    public static NcRecipeBuilder get(String id) {
        instance = new NcRecipeBuilder(id);
        return instance;
    }

    public NcRecipeBuilder items(List<Ingredient> input, List<Ingredient> output) {
        instance.inputItems = input;
        instance.outputItems = output;
        return instance;
    }

    public NcRecipeBuilder itemsString(List<Ingredient> input, List<String> output) {
        instance.inputItems = input;
        instance.outputItemsText = output;
        return instance;
    }


    public NcRecipeBuilder fluids(List<FluidStackIngredient> input, List<FluidStackIngredient> output) {
        instance.inputFluids = input;
        instance.outputFluids = output;
        return instance;
    }


    public NcRecipeBuilder modifiers(double timeModifier, double radiation, double powerModifier, double rarity) {
        this.timeModifier = timeModifier;
        this.radiation = radiation;
        this.powerModifier = powerModifier;
        this.rarityModifier = rarity;
        return this;
    }

    public NcRecipeBuilder modifiers(double timeModifier, double radiation, double powerModifier) {
        this.timeModifier = timeModifier;
        this.radiation = radiation;
        this.powerModifier = powerModifier;
        return this;
    }

    @Override
    protected NcRecipeResult getResult(ResourceLocation id) {
        return new NcRecipeResult(id);
    }

    public ResourceLocation getRecipeId() {
        StringBuilder name = new StringBuilder();
        for (Ingredient in : inputItems) {
            name.append(Arrays.stream(in.getItems()).toList().getFirst().getDisplayName()).append("-");
        }
        for (FluidStackIngredient in : inputFluids) {
            name.append(in.getName()).append("-");
        }
        if (useInputForId) {
            for (FluidStackIngredient out : outputFluids) {
                name.append(out.getName()).append("-");
            }
        }
        name.replace(name.length() - 1, name.length(), "");

        return ResourceLocation.fromNamespaceAndPath(MODID, ID + "/" + recipeIdReplacements(name.toString()));
    }

    protected String recipeIdReplacements(String val) {
        val = val.replace("nuclearcraft_", "");
        val = val.replace("depleted_fuel", "d_f");
        return val;
    }

    public void build(RecipeOutput consumer) {
        build(consumer, getRecipeId());
    }

    public NcRecipeBuilder temperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    public NcRecipeBuilder coolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
        return this;
    }

    public NcRecipeBuilder heatRequired(double heatRequired) {
        this.heatRequired = heatRequired;
        return this;
    }

    private boolean useInputForId = false;

    public NcRecipeBuilder useInputForId(boolean b) {
        useInputForId = b;
        return this;
    }

    public class NcRecipeResult extends RecipeResult {
        protected NcRecipeResult(ResourceLocation id) {
            super(id);
        }

        @Override
        public Advancement.Builder advancement() {
            return null;
        }

        @Override
        public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... iConditions) {

        }

//        @Override
//        public JsonObject serializeRecipe() {
//            JsonObject json = new JsonObject();
//            JsonArray inputJson = new JsonArray();
//
//            if (!inputItems.isEmpty()) {
//                for (Ingredient in : inputItems) {
//                    inputJson.add(serializeIngredient(in));
//                }
//                json.add("input", inputJson);
//            }
//
//            JsonArray outJson = new JsonArray();
//
//            if (!outputItems.isEmpty()) {
//                for (Ingredient out : outputItems) {
//                    outJson.add(serializeIngredient(out));
//                }
//                json.add("output", outJson);
//            }
//
//            if (!outputItemsText.isEmpty()) {
//                outJson = new JsonArray();
//                for (String out : outputItemsText) {
//                    JsonObject item = new JsonObject();
//                    item.addProperty("item", out);
//                    outJson.add(item);
//                }
//                json.add("output", outJson);
//            }
//
//            inputJson = new JsonArray();
//            for (FluidStackIngredient in : inputFluids) {
//                inputJson.add(in.serialize());
//            }
//            if (!inputFluids.isEmpty()) {
//                json.add("inputFluids", inputJson);
//            }
//
//            outJson = new JsonArray();
//            if (!outputFluids.isEmpty()) {
//                for (FluidStackIngredient out : outputFluids) {
//                    outJson.add(out.serialize());
//                }
//                json.add("outputFluids", outJson);
//            }
//            if (heatRequired > 0) {
//                json.addProperty("heatRequired", heatRequired);
//            }
//            if (coolingRate > 0) {
//                json.addProperty("coolingRate", coolingRate);
//            }
//            if (timeModifier > 0) {
//                json.addProperty("timeModifier", timeModifier);
//            }
//            if (radiation != 0) {
//                json.addProperty("radiation", radiation);
//            }
//            if (powerModifier > 0) {
//                json.addProperty("powerModifier", powerModifier);
//            }
//            if (rarityModifier != 1D && rarityModifier != 0) {
//                json.addProperty("rarityModifier", rarityModifier);
//            }
//            if (temperature != 0D) {
//                json.addProperty("temperature", temperature);
//            }
//            return json;
//        }
    }
}