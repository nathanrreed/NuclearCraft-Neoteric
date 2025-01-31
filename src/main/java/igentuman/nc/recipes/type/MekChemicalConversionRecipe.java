package igentuman.nc.recipes.type;

import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.recipe.ingredients.creator.FluidStackIngredientCreator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.Items.BUCKET;

public class MekChemicalConversionRecipe extends NcRecipe {

    public static class Type extends MekChemicalConversionRecipe {
        public Type() {
            super(new ItemStackIngredient[0], new ItemStackIngredient[0], new FluidStackIngredient[0], new FluidStackIngredient[0], 1, 1, 1, 1);
        }
    }

    public ChemicalStack inputChemical;
    public FluidStack outputFluid;

    public MekChemicalConversionRecipe(ItemStackIngredient[] input, ItemStackIngredient[] output, FluidStackIngredient[] inputFluids, FluidStackIngredient[] outputFluids, double timeModifier, double powerModifier, double radiation, double rarityModifier) {
        super(input, output, timeModifier, powerModifier, radiation, rarityModifier);
    }

    public MekChemicalConversionRecipe(ChemicalStack input, FluidStack outputFluid) {
        super(new ItemStackIngredient[0], new ItemStackIngredient[0], 1, 1, 1, 1);
        this.inputChemical = input;
        this.outputFluid = outputFluid;
    }

    public static FluidStack getStackByTagCode(String name) {
        TagKey<Fluid> fluidITag = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", name));
        FluidStack fluidStack = FluidStack.EMPTY;
        try {
            fluidStack = FluidStackIngredientCreator.INSTANCE.from(fluidITag, 1000).getRepresentations().get(0);
        } catch (Exception e) {
        }
        return fluidStack;
    }

    public static FluidStack getByChemical(Chemical chemical) {
        String name = chemical.getName();
        return getStackByTagCode(name);
    }

    public static List<MekChemicalConversionRecipe> getRecipes() {
        List<MekChemicalConversionRecipe> recipes = new ArrayList<>();

        for (Map.Entry<ResourceKey<Chemical>, Chemical> chemical : MekanismAPI.CHEMICAL_REGISTRY.entrySet()) {
            FluidStack fluid = getByChemical(chemical.getValue());
            if (fluid.isEmpty()) continue;
            recipes.add(new MekChemicalConversionRecipe(new ChemicalStack(chemical.getValue(), 1000), fluid));
        }

        return recipes;
    }

    @Override
    public @NotNull String getCodeId() {
        return "mek_chemical_conversion";
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(BUCKET);
    }
}
