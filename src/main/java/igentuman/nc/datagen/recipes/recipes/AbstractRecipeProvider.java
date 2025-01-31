package igentuman.nc.datagen.recipes.recipes;

import igentuman.nc.datagen.recipes.builder.NcRecipeBuilder;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.IngredientCreatorAccess;
import igentuman.nc.recipes.ingredient.creator.ItemStackIngredientCreator;
import igentuman.nc.setup.registration.FissionFuel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

import static igentuman.nc.NuclearCraft.rl;
import static igentuman.nc.setup.registration.FissionFuel.*;
import static igentuman.nc.setup.registration.NCFluids.ALL_FLUID_ENTRIES;
import static igentuman.nc.setup.registration.NCItems.*;
import static igentuman.nc.setup.registration.Tags.*;
import static net.minecraft.world.item.Items.AIR;
import static net.minecraft.world.item.Items.BARRIER;

public abstract class AbstractRecipeProvider {

    public static String ID;

    public static RecipeOutput consumer;
    private static List<ItemStackIngredient> input;
    private static List<ItemStackIngredient> output;
    private static double[] params;

    protected static ItemStackIngredient ingredient(TagKey<Item> tag, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(tag, count);
    }

    protected static ItemStackIngredient ingredient(Item item, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(item, count));
    }

    protected static ItemStackIngredient blockStack(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(blockItem(name), count));
    }

    protected static ItemStack stack(Item item, int count) {
        return new ItemStack(item, count);
    }

    protected static ItemStack stack(Block block, int count) {
        return new ItemStack(block, count);
    }

    protected static ItemStack stack(String item, int count) {
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item)), count);
    }

    public static ItemStack[] stackArray(ItemStack... stacks) {
        return stacks;
    }

    protected static void doubleToItem(String id, ItemStackIngredient input1, ItemStackIngredient input2, ItemStackIngredient output, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(id)
                .items(List.of(input1, input2), List.of(output))
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer);
    }

    protected static FluidStack fluidStack(Fluid fluid, int amount) {
        try {
            return IngredientCreatorAccess.fluid().from(fluid, amount).getRepresentations().getFirst();
        } catch (NullPointerException e) {
            throw new NullPointerException("Fluid " + fluid.getFluidType() + " does not exist");
        }
    }

    protected static FluidStack fluidStack(String name, int amount) {
        try {
            return IngredientCreatorAccess.fluid().from(name, amount).getRepresentations().getFirst();
        } catch (NullPointerException e) {
            System.out.println("Fluid " + name + " does not exist");
        }
        return FluidStack.EMPTY;
    }

    protected static FluidStackIngredient fluidIngredient(String name, int amount) {
        return IngredientCreatorAccess.fluid().from(forgeFluid(name), amount);
    }

    protected static FluidStackIngredient fluidStackIngredient(String name, int amount) {
        return IngredientCreatorAccess.fluid().from(fluidStack(name, amount));
    }

    public static void itemToItem(ItemStackIngredient input, ItemStackIngredient output, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(ID)
                .items(List.of(input), List.of(output))
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer);
    }

    public static void itemsToItems(List<ItemStackIngredient> input, List<ItemStackIngredient> output, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(ID)
                .items(input, output)
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer);
    }

    public static void itemsToItemsString(List<ItemStackIngredient> input, List<String> output, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(ID)
                .itemsString(input, output)
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer);
    }

    public static void oreVein(List<ItemStackIngredient> input, ItemStackIngredient output, String nameKey, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        double rarity = params.length > 3 ? params[3] : 1.0;
        NcRecipeBuilder.get(ID)
                .items(input, List.of(output))
                .modifiers(timeModifier, radiation, powerModifier, rarity)
                .build(consumer, rl(ID + "/" + nameKey));
    }


    public static void fluidsAndFluids(List<FluidStackIngredient> input, List<FluidStackIngredient> output, double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(ID)
                .fluids(input, output)
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer);
    }

    public static void coolantRecipe(List<FluidStackIngredient> input, List<FluidStackIngredient> output, double coolingRate) {
        NcRecipeBuilder.get(ID)
                .fluids(input, output)
                .modifiers(0, 0, 0, 0)
                .coolingRate(coolingRate)
                .build(consumer);
    }

    public static void boilingRecipe(List<FluidStackIngredient> input, List<FluidStackIngredient> output, double heatRequired) {
        NcRecipeBuilder.get(ID)
                .fluids(input, output)
                .modifiers(0, 0, 0, 0)
                .heatRequired(heatRequired)
                .useInputForId(true)
                .build(consumer);
    }

    public static void itemsAndFluids(
            List<ItemStackIngredient> inputItems, List<ItemStackIngredient> outputItems,
            List<FluidStackIngredient> inputFluids, List<FluidStackIngredient> outputFluids,
            double... params) {
        double timeModifier = params.length > 0 ? params[0] : 1.0;
        double powerModifier = params.length > 1 ? params[1] : 1.0;
        double radiation = params.length > 2 ? params[2] : 1.0;
        NcRecipeBuilder.get(ID)
                .items(inputItems, outputItems)
                .fluids(inputFluids, outputFluids)
                .modifiers(timeModifier, radiation, powerModifier)
                .build(consumer);
    }

    public static TagKey<Fluid> forgeFluid(String name) {
        String key = "c";
        if (name.contains(":")) {
            key = name.split(":")[0];
            name = name.split(":")[1];
        }
        return FluidTags.create(ResourceLocation.fromNamespaceAndPath(key, name));
    }

    public static Item blockItem(String name) {
        for (String key : List.of(name, "block_" + name, name + "_block")) {
            if (ALL_NC_ITEMS.get(name) != null) {
                return ALL_NC_ITEMS.get(key).get();
            }
        }
        System.out.println("null block: " + name);
        return BARRIER;
    }

    public static Item nuggetItem(String name) {
        if (NC_NUGGETS.get(name) == null) {
            System.out.println("null nugget: " + name);
        }
        return NC_NUGGETS.get(name).get();
    }

    public static Item dustItem(String name) {
        if (NC_DUSTS.get(name) == null) {
            System.out.println("null dust: " + name);
        }
        return NC_DUSTS.get(name).get();
    }

    public static TagKey<Item> dustTag(String name) {
        if (DUSTS_TAG.get(name) == null) {
            System.out.println("null dust tag: " + name);
        }
        return DUSTS_TAG.get(name);
    }

    public static ItemStackIngredient dustStack(String name, int count) {
        return ItemStackIngredientCreator.INSTANCE.from(stack(dustItem(name), count));
    }

    public static ItemStackIngredient nuggetStack(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(nuggetItem(name), count));
    }

    public static ItemStackIngredient ingotStack(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(ingotItem(name), count));
    }

    public static ItemStackIngredient gemStack(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(gemItem(name), count));
    }

    public static ItemStackIngredient plateStack(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(plateItem(name), count));
    }

    public static ItemStackIngredient isotopeStack(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ItemStackIngredientCreator.INSTANCE.from(stack(isotopeItem(name), count));
    }

    static ItemStackIngredient blockIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgeBlock(name), count);
    }

    public static ItemStackIngredient dustIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgeDust(name), count);
    }

    public static FluidStackIngredient moltenFuelIngredient(List<String> name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return IngredientCreatorAccess.fluid().from(ALL_FLUID_ENTRIES.get(ResourceLocation.parse(fuelItem(name).toString()).getPath()).getStill(), count);
    }

    public static ItemStackIngredient fuelIngredient(List<String> name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(fuelItem(name), count);
    }

    public static Item fuelItem(List<String> name) {
        if (NC_FUEL.get(name) != null) {
            return NC_FUEL.get(name).get();
        }
        if (NC_DEPLETED_FUEL.get(name) != null) {
            return NC_DEPLETED_FUEL.get(name).get();
        }
        System.out.println("null fuel: " + String.join("-", name));
        return AIR;
    }


    public static ItemStackIngredient isotopeIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(isotopeItem(name), count);
    }

    public static ItemStackIngredient oreIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgeOre(name), count);
    }

    public static ItemStackIngredient chunkIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgeChunk(name), count);
    }

    public static ItemStackIngredient ingotIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgeIngot(name), count);
    }

    public static ItemStackIngredient plateIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgePlate(name), count);
    }


    public static ItemStackIngredient gemIngredient(String name, int... pCount) {
        int count = pCount.length > 0 ? pCount[0] : 1;
        return ingredient(forgeGem(name), count);
    }

    public static Item isotopeItem(String name) {
        if (NC_ISOTOPES.get(name) == null) {
            System.out.println("null isotope: " + name);
        }
        return NC_ISOTOPES.get(name).get();
    }

    public static Item plateItem(String name) {
        if (NC_PLATES.get(name) == null) {
            System.out.println("null plate: " + name);
        }
        return NC_PLATES.get(name).get();
    }

    public static TagKey<Item> plateTag(String name) {
        if (PLATES_TAG.get(name) == null) {
            System.out.println("null plate tag: " + name);
        }
        return PLATES_TAG.get(name);
    }

    public static Item ingotItem(String name) {
        if (NC_INGOTS.get(name) == null) {
            System.out.println("null ingot: " + name);
        }
        return NC_INGOTS.get(name).get();
    }

    public static TagKey<Item> ingotTag(String name) {
        if (INGOTS_TAG.get(name) == null) {
            System.out.println("null ingot tag: " + name);
        }
        return INGOTS_TAG.get(name);
    }

    public static TagKey<Item> gemTag(String name) {
        if (GEMS_TAG.get(name) == null) {
            System.out.println("null gem tag: " + name);
        }
        return GEMS_TAG.get(name);
    }

    public static Item gemItem(String name) {
        if (NC_GEMS.get(name) == null) {
            System.out.println("null gem: " + name);
        }
        return NC_GEMS.get(name).get();
    }

    public static Item getIsotope(String name, String id, String type) {
        if (!type.isEmpty()) {
            type = "_" + type;
        }
        if (!FissionFuel.NC_ISOTOPES.containsKey(name + "/" + id + type)) {
            for (String isotope : FissionFuel.NC_ISOTOPES.keySet()) {
                if (isotope.contains(id)) {
                    return FissionFuel.NC_ISOTOPES.get(isotope).get();
                }
            }
        }
        return FissionFuel.NC_ISOTOPES.get(name + "/" + id + type).get();
    }
}
