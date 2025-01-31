package igentuman.nc.datagen.recipes.builder;

import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.type.NcRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static igentuman.nc.NuclearCraft.MODID;

public class NcRecipeBuilder extends RecipeBuilder<NcRecipeBuilder> {

    private List<ItemStackIngredient> inputItems = List.of();
    private List<ItemStackIngredient> outputItems = List.of();
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

    public NcRecipeBuilder items(List<ItemStackIngredient> input, List<ItemStackIngredient> output) {
        instance.inputItems = input;
        instance.outputItems = output;
        return instance;
    }

    public NcRecipeBuilder itemsString(List<ItemStackIngredient> input, List<String> output) {
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

    public ResourceLocation getRecipeId() {
        StringBuilder name = new StringBuilder();
        for (ItemStackIngredient in : inputItems) {
            name.append(in.getName()).append("-");
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

    public net.minecraft.data.recipes.RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public net.minecraft.data.recipes.RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return outputItems.isEmpty() ? Items.AIR : this.outputItems.getFirst().getInputsRaw().getFirst().getItems()[0].getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        NcRecipe recipe = new NcRecipe(inputItems.stream().toArray(ItemStackIngredient[]::new), outputItems.stream().toArray(ItemStackIngredient[]::new), inputFluids.toArray(FluidStackIngredient[]::new), outputFluids.toArray(FluidStackIngredient[]::new), timeModifier, powerModifier, radiation, rarityModifier) {
            @Override
            public String getCodeId() {
                return ID;
            }
        };
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }

    @Override
    public void build(RecipeOutput consumer, ResourceLocation id) {
        validate(id);
        save(consumer, id);
    }
}