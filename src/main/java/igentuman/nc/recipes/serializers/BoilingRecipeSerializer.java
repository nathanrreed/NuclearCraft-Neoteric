package igentuman.nc.recipes.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import igentuman.nc.recipes.type.NcRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class BoilingRecipeSerializer<RECIPE extends NcRecipe> extends NcRecipeSerializer<RECIPE> {

    public BoilingRecipeSerializer(IFactory factory) {
        super(factory);
    }

//    @Override
//    public @NotNull RECIPE fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
//
//        FluidStackIngredient[] inputFluids = inputFluidsFromJson(json, recipeId);
//        FluidStackIngredient[] outputFluids = outputFluidsFromJson(json, recipeId);
//
//        double heatRequired = 1D;
//        try {
//            heatRequired = GsonHelper.getAsDouble(json, "heatRequired", 1D);
//        } catch (Exception ex) {
//            NuclearCraft.LOGGER.warn("Unable to parse params for recipe: "+recipeId);
//        }
//        return this.factory.create(recipeId, new ItemStackIngredient[]{}, new ItemStackIngredient[]{}, inputFluids, outputFluids, heatRequired, 1, 1, 1);
//    }
//
//
//    @Override
//    public RECIPE fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
//        try {
//            ItemStackIngredient[] inputItems = readItems(buffer);
//            ItemStackIngredient[] outputItems = readItems(buffer);
//            FluidStackIngredient[] inputFluids = readFluids(buffer);
//            FluidStackIngredient[] outputFluids = readFluids(buffer);
//
//            double heatRequired = buffer.readDouble();
//            double powerModifier = buffer.readDouble();
//            double radiation = buffer.readDouble();
//
//            return this.factory.create(recipeId, new ItemStackIngredient[]{}, new ItemStackIngredient[]{}, inputFluids,  outputFluids, heatRequired, 1, 1, 1);
//        } catch (Exception e) {
//            NuclearCraft.LOGGER.error("NC fromNetwork recipe error: " + recipeId);
//            throw e;
//        }
//    }
}
