package igentuman.nc.block.entity.processor;

import com.mojang.datafixers.util.Either;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.handler.OreVeinProvider;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.FluidStackIngredientCreator;
import igentuman.nc.recipes.type.NcRecipe;
import igentuman.nc.recipes.type.OreVeinRecipe;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;

import static net.minecraft.world.item.Items.FILLED_MAP;
import static net.minecraft.world.item.Items.PAPER;

public class AnalyzerBE extends NCProcessorBE<AnalyzerBE.Recipe> {
    public AnalyzerBE(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, Processors.ANALYZER);
    }

    public HashMap<Long, RecipeHolder<OreVeinRecipe>> veinsCache = new HashMap<>();
    private BlockPos alreadySearched;

    @Override
    public String getName() {
        return Processors.ANALYZER;
    }

    @NothingNullByDefault
    public static class Recipe extends NcRecipe {
        public Recipe(List<ItemStackIngredient> input, List<ItemStackIngredient> output, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> inputFluids, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> outputFluids, double timeModifier, double powerModifier, double heatModifier, double rarity) {
            super(input, output, inputFluids, outputFluids, timeModifier, powerModifier, heatModifier, 1);
        }

        @Override
        public String getCodeId() {
            return Processors.ANALYZER;
        }
    }

    public void tickServer() {
        if (worldPosition.equals(alreadySearched)) {
            return;
        }
        super.tickServer();
    }


    protected void handleRecipeOutput() {
        if (hasRecipe() && recipeInfo.isCompleted()) {
            handleChunkAnalyzeWithPaper();
            handleMapAnalyze();
            if (recipe.handleOutputs(contentHandler)) {
                recipeInfo.clear();
            } else {
                recipeInfo.stuck = true;
            }
        }
    }

    private void handleMapAnalyze() {
        if (recipe.getInputIngredient(0).test(new ItemStack(FILLED_MAP))) {
            for (ItemStack output : recipe.getResultItems()) {
                output.set(DataComponents.CUSTOM_DATA, contentHandler.itemHandler.holdedInputs.getFirst().get(DataComponents.CUSTOM_DATA));
                output.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putBoolean("is_nc_analyzed", true)));
            }
        }
    }

    private void handleChunkAnalyzeWithPaper() {
        if (recipe.getInputIngredient(0).test(new ItemStack(PAPER))) {
            RecipeHolder<OreVeinRecipe> vein = getVein();
            alreadySearched = worldPosition;
            if (vein == null) {
                for (ItemStack output : recipe.getResultItems()) {
                    output.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putString("vein", "nc.ore_vein.none")));
                }
            } else {
                for (ItemStack output : recipe.getResultItems()) {
                    output.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> compoundTag.putString("vein", "nc.ore_vein." + vein.id().getPath().replace("nc_ore_veins/", ""))));
                }
            }
            for (ItemStack output : recipe.getResultItems()) {
                output.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(compoundTag -> {
                    compoundTag.putLong("pos", worldPosition.asLong());
                    compoundTag.putBoolean("is_nc_analyzed", true);
                }));
            }
        }
    }

    protected RecipeHolder<OreVeinRecipe> getVein() {
        long pos = ChunkPos.asLong(worldPosition);
        if (!veinsCache.containsKey(pos)) {
            veinsCache.put(pos, OreVeinProvider.get((ServerLevel) level).getVeinForChunk(ChunkPos.getX(pos), ChunkPos.getZ(pos)));
        }
        return veinsCache.get(pos);
    }
}
