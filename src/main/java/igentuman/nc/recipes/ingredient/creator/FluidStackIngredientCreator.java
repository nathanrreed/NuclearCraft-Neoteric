package igentuman.nc.recipes.ingredient.creator;

import igentuman.nc.NuclearCraft;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.IMultiIngredient;
import igentuman.nc.recipes.ingredient.InputIngredient;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NothingNullByDefault
public class FluidStackIngredientCreator implements IFluidStackIngredientCreator {

    public static final FluidStackIngredientCreator INSTANCE = new FluidStackIngredientCreator();

    private FluidStackIngredientCreator() {
    }

    @Override
    public FluidStackIngredient from(FluidStack instance) {
        Objects.requireNonNull(instance, "FluidStackIngredients cannot be created from a null FluidStack.");
        if (instance.isEmpty()) {
            // throw new IllegalArgumentException("FluidStackIngredients cannot be created using the empty stack.");
        }
        //Copy the stack to ensure it doesn't get modified afterwards
        return new SingleFluidStackIngredient(instance.copy());
    }

    @Override
    public FluidStackIngredient from(TagKey<Fluid> tag, int amount) {
        Objects.requireNonNull(tag, "FluidStackIngredients cannot be created from a null tag.");
        if (amount <= 0) {
            throw new IllegalArgumentException("FluidStackIngredients must have an amount of at least one. Received size was: " + amount);
        }
        return new TaggedFluidStackIngredient(tag, amount);
    }

    public FluidStackIngredient from(ResourceLocation tag, int amount) {
        return from(FluidTags.create(tag), amount);
    }

    public FluidStackIngredient from(List<FluidStack> ingredients) {
        if (ingredients.size() == 1) {
            return new SingleFluidStackIngredient(ingredients.getFirst());
        }
        return new MultiFluidStackIngredient(ingredients);
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Converts a stream of ingredients into a single ingredient by converting the stream to an array and calling {@link #createMulti(FluidStackIngredient[])}.
     */
    @Override
    public FluidStackIngredient createMulti(FluidStackIngredient... ingredients) {
        Objects.requireNonNull(ingredients, "Cannot create a multi ingredient out of a null array.");
        if (ingredients.length == 0) {
            throw new IllegalArgumentException("Cannot create a multi ingredient out of no ingredients.");
        } else if (ingredients.length == 1) {
            return ingredients[0];
        }
        List<FluidStackIngredient> cleanedIngredients = new ArrayList<>();
        for (FluidStackIngredient ingredient : ingredients) {
            if (ingredient instanceof MultiFluidStackIngredient multi) {
                //Don't worry about if our inner ingredients are multi as well, as if this is the only external method for
                // creating a multi ingredient, then we are certified they won't be of a higher depth
                Collections.addAll(cleanedIngredients, multi.ingredients);
            } else {
                cleanedIngredients.add(ingredient);
            }
        }
        //There should be more than a single fluid, or we would have split out earlier
        return new MultiFluidStackIngredient(cleanedIngredients.toArray(new FluidStackIngredient[0]));
    }

    @Override
    public FluidStackIngredient from(Stream<FluidStackIngredient> ingredients) {
        return createMulti(ingredients.toArray(FluidStackIngredient[]::new));
    }

    @NothingNullByDefault
    public static class SingleFluidStackIngredient extends FluidStackIngredient {

        private final FluidStack fluidInstance;

        public SingleFluidStackIngredient(FluidStack fluidInstance) {
            this.fluidInstance = Objects.requireNonNull(fluidInstance);
        }

        @Override
        public String getName() {
            return fluidInstance.getFluid().getFluidType().toString().replace(":", "_");
        }

        @Override
        public boolean test(FluidStack fluidStack) {
            return testType(fluidStack) && fluidStack.getAmount() >= fluidInstance.getAmount();
        }

        @Override
        public boolean testType(FluidStack fluidStack) {
            return FluidStack.isSameFluidSameComponents(Objects.requireNonNull(fluidStack), fluidInstance);
        }

        @Override
        public FluidStack getMatchingInstance(FluidStack fluidStack) {
            return test(fluidStack) ? fluidInstance.copy() : FluidStack.EMPTY;
        }

        @Override
        public long getNeededAmount(FluidStack stack) {
            return testType(stack) ? fluidInstance.getAmount() : 0;
        }

        @Override
        public boolean hasNoMatchingInstances() {
            return false;
        }

        @Override
        public List<@NotNull FluidStack> getRepresentations() {
            return Collections.singletonList(fluidInstance);
        }

        /**
         * For use in recipe input caching. Do not use this to modify the backing stack.
         */
        public FluidStack getInputRaw() {
            return fluidInstance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SingleFluidStackIngredient other = (SingleFluidStackIngredient) o;
            //Need to use this over equals to ensure we compare amounts
            return FluidStack.isSameFluidSameComponents(fluidInstance, other.fluidInstance);
        }

        @Override
        public int hashCode() {
            return fluidInstance.hashCode();
        }

        @Override
        public List<FluidStack> getInputsRaw() {
            return List.of(fluidInstance);
        }
    }

    @NothingNullByDefault
    public static class TaggedFluidStackIngredient extends FluidStackIngredient {
        protected final HolderSet.Named<Fluid> tag;

        private TaggedFluidStackIngredient(TagKey<Fluid> tag, int amount) {
            this(BuiltInRegistries.FLUID.getOrCreateTag(tag), amount);
        }

        private TaggedFluidStackIngredient(HolderSet.Named<Fluid> tag, int amount) {
            this.tag = tag;
            this.amount = amount;
        }

        public TaggedFluidStackIngredient(ResourceLocation resourceLocation, Integer amount) {
            this(FluidTags.create(resourceLocation), amount);
        }

        public String getName() {
            return tag.key().location().getPath().replace('/', '_').replace(':', '.');
        }

        @Override
        public boolean test(FluidStack fluidStack) {
            return testType(fluidStack) && fluidStack.getAmount() >= amount;
        }

        @Override
        public boolean testType(FluidStack fluidStack) {
            return tag.contains(Objects.requireNonNull(fluidStack).getFluidHolder());
        }

        @Override
        public FluidStack getMatchingInstance(FluidStack fluidStack) {
            if (test(fluidStack)) {
                //Our fluid is in the tag, so we make a new stack with the given amount
                return new FluidStack(fluidStack.getFluid(), amount);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public long getNeededAmount(FluidStack stack) {
            return testType(stack) ? amount : 0;
        }

        @Override
        public boolean hasNoMatchingInstances() {
            return tag.size() == 0;
        }

        @Override
        public List<@NotNull FluidStack> getRepresentations() {
            //TODO: Can this be cached some how
            List<@NotNull FluidStack> representations = new ArrayList<>();
            for (Holder<Fluid> fluid : tag.stream().toList()) {
                representations.add(new FluidStack(fluid, amount));
            }
            if (representations.isEmpty()) {
                NuclearCraft.LOGGER.error("Fluid Tag {} is empty!", tag.key().location());
            }
            return representations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TaggedFluidStackIngredient other = (TaggedFluidStackIngredient) o;
            return amount == other.amount && tag.equals(other.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, amount);
        }

        public ResourceLocation getKey() {
            return tag.key().location();
        }

        @Override
        public List<FluidStack> getInputsRaw() {
            return null; // Should never be called
        }
    }

    @NothingNullByDefault
    public static class MultiFluidStackIngredient extends FluidStackIngredient implements IMultiIngredient<FluidStack, FluidStackIngredient> {

        public final FluidStackIngredient[] ingredients;

        private MultiFluidStackIngredient(FluidStackIngredient... ingredients) {
            this.ingredients = ingredients;
        }

        public MultiFluidStackIngredient(List<FluidStack> fluidStacks) {
            this.ingredients = ((FluidStackIngredientCreator.MultiFluidStackIngredient) FluidStackIngredientCreator.INSTANCE.createMulti(fluidStacks.stream().map(SingleFluidStackIngredient::new).toArray(FluidStackIngredient[]::new))).ingredients;
        }

        @Override
        public boolean test(FluidStack stack) {
            return Arrays.stream(ingredients).anyMatch(ingredient -> ingredient.test(stack));
        }

        @Override
        public boolean testType(FluidStack stack) {
            return Arrays.stream(ingredients).anyMatch(ingredient -> ingredient.testType(stack));
        }

        @Override
        public FluidStack getMatchingInstance(FluidStack stack) {
            for (FluidStackIngredient ingredient : ingredients) {
                FluidStack matchingInstance = ingredient.getMatchingInstance(stack);
                if (!matchingInstance.isEmpty()) {
                    return matchingInstance;
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public long getNeededAmount(FluidStack stack) {
            for (FluidStackIngredient ingredient : ingredients) {
                long amount = ingredient.getNeededAmount(stack);
                if (amount > 0) {
                    return amount;
                }
            }
            return 0;
        }

        @Override
        public boolean hasNoMatchingInstances() {
            return Arrays.stream(ingredients).allMatch(InputIngredient::hasNoMatchingInstances);
        }

        @Override
        public List<@NotNull FluidStack> getRepresentations() {
            List<@NotNull FluidStack> representations = new ArrayList<>();
            for (FluidStackIngredient ingredient : ingredients) {
                representations.addAll(ingredient.getRepresentations());
            }
            return representations;
        }

        @Override
        public boolean forEachIngredient(Predicate<FluidStackIngredient> checker) {
            boolean result = false;
            for (FluidStackIngredient ingredient : ingredients) {
                result |= checker.test(ingredient);
            }
            return result;
        }

        @Override
        public final List<FluidStackIngredient> getIngredients() {
            return List.of(ingredients);
        }

        @Override
        public String getName() {
            return getRepresentations().getFirst().getFluid().getFluidType().toString().split(":")[1];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return Arrays.equals(ingredients, ((MultiFluidStackIngredient) o).ingredients);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(ingredients);
        }

        @Override
        public List<FluidStack> getInputsRaw() {
            return Arrays.stream(ingredients).map(single -> ((SingleFluidStackIngredient) single).fluidInstance).toList();
        }
    }

    private enum IngredientType {
        SINGLE,
        TAGGED,
        MULTI
    }
}