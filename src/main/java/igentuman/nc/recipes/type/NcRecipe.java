package igentuman.nc.recipes.type;

import com.mojang.datafixers.util.Either;
import igentuman.nc.recipes.AbstractRecipe;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.FluidStackIngredientCreator;
import igentuman.nc.recipes.ingredient.creator.IngredientCreatorAccess;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

import static igentuman.nc.compat.GlobalVars.CATALYSTS;
import static igentuman.nc.compat.GlobalVars.RECIPE_CLASSES;
import static net.minecraft.world.item.Items.BARRIER;

@NothingNullByDefault
public abstract class NcRecipe extends AbstractRecipe {

    public final double rarityModifier;

    public NcRecipe(
            ItemStackIngredient[] inputItems,
            ItemStackIngredient[] outputItems,
            FluidStackIngredient[] inputFluids,
            FluidStackIngredient[] outputFluids,
            double timeModifier,
            double powerModifier,
            double radiationModifier,
            double rarityModifier
    ) {

        super();
        this.inputItems = inputItems;
        this.outputItems = outputItems;
        this.inputFluids = inputFluids;
        this.outputFluids = outputFluids;

        this.timeModifier = timeModifier;
        this.powerModifier = powerModifier;
        this.radiationModifier = radiationModifier;
        this.rarityModifier = rarityModifier;
        CATALYSTS.put(getCodeId(), List.of(getToastSymbol()));
        RECIPE_CLASSES.put(getCodeId(), getClass());
    }

    public NcRecipe(List<ItemStackIngredient> inputItems, List<ItemStackIngredient> outputItems, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> inputFluids, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> outputFluids, double timeModifier, double powerModifier, double radiationModifier, double rarityModifier) {
        this(inputItems.toArray(ItemStackIngredient[]::new), outputItems.toArray(ItemStackIngredient[]::new), inputFluids.stream().map(i -> i.right().isPresent() ? i.right().get() : i.left().get()).toArray(FluidStackIngredient[]::new), outputFluids.stream().map(i -> i.right().isPresent() ? i.right().get() : i.left().get()).toArray(FluidStackIngredient[]::new), timeModifier, powerModifier, radiationModifier, rarityModifier);
    }

    public NcRecipe(
            ItemStackIngredient[] inputItems,
            ItemStackIngredient[] outputItems,
            double timeModifier,
            double powerModifier,
            double radiationModifier,
            double rarityModifier
    ) {
        this(inputItems, outputItems, new FluidStackIngredient[0], new FluidStackIngredient[0], timeModifier, powerModifier, radiationModifier, rarityModifier);
    }

    public NcRecipe(
            FluidStackIngredient[] inputFluids,
            FluidStackIngredient[] outputFluids,
            double timeModifier,
            double powerModifier,
            double radiationModifier,
            double rarityModifier
    ) {
        this(new ItemStackIngredient[0], new ItemStackIngredient[0], inputFluids, outputFluids, timeModifier, powerModifier, radiationModifier, rarityModifier);
    }

    public static ItemStackIngredient getBarrier() {
        return IngredientCreatorAccess.item().from(new ItemStack(BARRIER));
    }

    protected FluidStackIngredient getEmptyFluid() {
        return IngredientCreatorAccess.fluid().from(FluidStack.EMPTY);
    }

    public List<ItemStackIngredient> inputItems() {
        return Arrays.stream(inputItems).toList();
    }

    public List<ItemStackIngredient> outputItems() {
        return Arrays.stream(outputItems).toList();
    }

    public List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> inputFluids() {
        return Arrays.stream(inputFluids).map(i -> i instanceof FluidStackIngredientCreator.TaggedFluidStackIngredient ? Either.<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>left((FluidStackIngredientCreator.TaggedFluidStackIngredient) i) : Either.<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>right(i)).toList();
    }

    public List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> outputFluids() {
        return Arrays.stream(outputFluids).map(i -> i instanceof FluidStackIngredientCreator.TaggedFluidStackIngredient ? Either.<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>left((FluidStackIngredientCreator.TaggedFluidStackIngredient) i) : Either.<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>right(i)).toList();
    }

    public Double timeModifier() {
        return timeModifier;
    }

    public Double powerModifier() {
        return powerModifier;
    }

    public Double radiationModifier() {
        return radiationModifier;
    }

    public Double rarityModifier() {
        return rarityModifier;
    }
}