package igentuman.nc.block.entity.processor;

import com.mojang.datafixers.util.Either;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.FluidStackIngredientCreator;
import igentuman.nc.recipes.type.NcRecipe;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class IngotFormerBE extends NCProcessorBE<IngotFormerBE.Recipe> {

    public IngotFormerBE(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, Processors.INGOT_FORMER);
    }

    @Override
    public String getName() {
        return Processors.INGOT_FORMER;
    }

    @NothingNullByDefault
    public static class Recipe extends NcRecipe {
        public Recipe(List<ItemStackIngredient> input, List<ItemStackIngredient> output, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> inputFluids, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> outputFluids, double timeModifier, double powerModifier, double heatModifier, double rarity) {
            super(input, output, inputFluids, outputFluids, timeModifier, powerModifier, heatModifier, 1);
        }

        @Override
        public String getCodeId() {
            return Processors.INGOT_FORMER;
        }
    }
}