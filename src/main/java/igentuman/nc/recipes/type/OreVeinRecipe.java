package igentuman.nc.recipes.type;

import com.mojang.datafixers.util.Either;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.handler.OreVeinProvider;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.FluidStackIngredientCreator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static igentuman.nc.setup.registration.NCItems.NC_PARTS;

public class OreVeinRecipe extends NcRecipe {
    public OreVeinRecipe(List<ItemStackIngredient> inputItems, List<ItemStackIngredient> outputItems, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> inputFluids, List<Either<FluidStackIngredientCreator.TaggedFluidStackIngredient, FluidStackIngredient>> outputFluids, double timeModifier, double powerModifier, double radiationModifier, double rarityModifier) {
        super(inputItems, outputItems, inputFluids, outputFluids, timeModifier, powerModifier, radiationModifier, rarityModifier);
    }

    @Override
    public String getCodeId() {
        return Processors.ANALYZER;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(NC_PARTS.get("research_paper").get());
    }

    public ItemStack getRandomOre(ServerLevel level, int x, int z, int id) {
        int score = OreVeinProvider.get(level).rand(x, z, id).nextInt(100);
        return getOreByScore(score, level, x, z);
    }

    public ItemStack getOreByScore(int score, ServerLevel level, int x, int z) {
        int id = OreVeinProvider.get(level).rand(x, z, score).nextInt(inputItems.length);
        for (int i = id; i < inputItems.length; i++) {
            if (score <= inputItems[i].getRepresentations().getFirst().getCount()) {
                return inputItems[i].getRepresentations().getFirst();
            }
            score -= inputItems[i].getRepresentations().getFirst().getCount();
        }
        return getOreByScore(score, level, x, z);
    }
}