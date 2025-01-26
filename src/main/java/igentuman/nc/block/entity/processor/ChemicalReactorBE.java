package igentuman.nc.block.entity.processor;

import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.type.NcRecipe;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalReactorBE extends NCProcessorBE<ChemicalReactorBE.Recipe> {
    public ChemicalReactorBE(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, Processors.CHEMICAL_REACTOR);
    }

    @Override
    public String getName() {
        return Processors.CHEMICAL_REACTOR;
    }

    @NothingNullByDefault
    public static class Recipe extends NcRecipe {
        public Recipe(                      ItemStackIngredient[] input, ItemStackIngredient[] output,
                      FluidStackIngredient[] inputFluids, FluidStackIngredient[] outputFluids,
                      double timeModifier, double powerModifier, double heatModifier, double rarity) {
            super(input, output, inputFluids, outputFluids, timeModifier, powerModifier, heatModifier, 1);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            //TODO
        }
    }
}
