package igentuman.nc.recipes.type;

import igentuman.nc.handler.OreVeinProvider;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static igentuman.nc.setup.registration.NCItems.NC_PARTS;

public class OreVeinRecipe extends NcRecipe {
    public OreVeinRecipe(ItemStackIngredient[] input, ItemStackIngredient[] output, FluidStackIngredient[] inputFluids, FluidStackIngredient[] outputFluids, double timeModifier, double powerModifier, double radiation, double rarityModifier) {
        super(input, output, timeModifier, powerModifier, radiation, rarityModifier);
    }

    @Override
    public @NotNull String getGroup() {
        return "research_paper";
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

    @Override
    public void write(FriendlyByteBuf buffer) {
//        super.write(buffer); //TODO
        buffer.writeDouble(rarityModifier);
    }
}
