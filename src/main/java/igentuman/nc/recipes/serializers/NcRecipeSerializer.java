package igentuman.nc.recipes.serializers;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function8;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.ingredient.creator.FluidStackIngredientCreator;
import igentuman.nc.recipes.ingredient.creator.FluidStackIngredientCreator.TaggedFluidStackIngredient;
import igentuman.nc.recipes.ingredient.creator.ItemStackIngredientCreator;
import igentuman.nc.recipes.type.NcRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class NcRecipeSerializer<RECIPE extends NcRecipe> implements RecipeSerializer<RECIPE> {
    IFactory<RECIPE> factory;

    public NcRecipeSerializer(IFactory<RECIPE> factory) {
        assert factory != null;
        this.factory = factory;
    }

    public static MapCodec<ItemStackIngredient> ITEM_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(ItemStackIngredient::getInputsRaw),
            Codec.INT.fieldOf("amount").forGetter(ItemStackIngredient::getAmount)).apply(inst, ItemStackIngredientCreator.INSTANCE::from));

    public static MapCodec<FluidStackIngredient> FLUID_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                    FluidStack.CODEC.listOf().fieldOf("ingredients").forGetter(FluidStackIngredient::getInputsRaw))
            .apply(inst, FluidStackIngredientCreator.INSTANCE::from));

    public static MapCodec<TaggedFluidStackIngredient> TAG_FLUID = RecordCodecBuilder.mapCodec(inst -> inst.group(
                    ResourceLocation.CODEC.fieldOf("tag").forGetter(TaggedFluidStackIngredient::getKey),
                    Codec.INT.fieldOf("amount").forGetter(TaggedFluidStackIngredient::getAmount))
            .apply(inst, TaggedFluidStackIngredient::new));

    public static MapCodec<Either<TaggedFluidStackIngredient, FluidStackIngredient>> ANY_FLUID = Codec.mapEither(TAG_FLUID, FLUID_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStackIngredient>> LIST_INGREDIENT_STACK = new StreamCodec<>() {
        @Override
        public @NotNull List<ItemStackIngredient> decode(RegistryFriendlyByteBuf buffer) {
            return Arrays.stream(buffer.readArray(ItemStackIngredient[]::new, buf -> ItemStackIngredientCreator.INSTANCE.from(
                    Ingredient.CONTENTS_STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE)),
                    ByteBufCodecs.INT.decode(buf))
            )).toList();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, List<ItemStackIngredient> value) {
            buffer.writeArray(value.toArray(), (buf, ing) -> {
                Ingredient.CONTENTS_STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE), ((ItemStackIngredient) ing).getInputsRaw().getFirst());
                ByteBufCodecs.INT.encode(buf, ((ItemStackIngredient) ing).getAmount());
            });
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<Either<TaggedFluidStackIngredient, FluidStackIngredient>>> LIST_FLUID_INGREDIENT_STACK = new StreamCodec<>() {
        @Override
        public @NotNull List<Either<TaggedFluidStackIngredient, FluidStackIngredient>> decode(RegistryFriendlyByteBuf buffer) {
            int read_index = buffer.readerIndex();
            try {
                return Arrays.stream(buffer.readArray(FluidStackIngredient[]::new, buf -> FluidStackIngredientCreator.INSTANCE.from(
                        ResourceLocation.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE)),
                        ByteBufCodecs.INT.decode(buf))
                )).map(i -> Either.<TaggedFluidStackIngredient, FluidStackIngredient>left((TaggedFluidStackIngredient) i)).toList();
            } catch (Exception exception) {
                buffer.setIndex(read_index, buffer.writerIndex());
                return Arrays.stream(buffer.readArray(FluidStackIngredient[]::new, buf -> FluidStackIngredientCreator.INSTANCE.from(
                        FluidStack.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE)))
                )).map(Either::<TaggedFluidStackIngredient, FluidStackIngredient>right).toList();
            }
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, List<Either<TaggedFluidStackIngredient, FluidStackIngredient>> value) {
            int write_index = buffer.writerIndex();
            try {
                buffer.writeArray(value.toArray(), (buf, ing) -> {
                    ResourceLocation.STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE), ((TaggedFluidStackIngredient) ((Either) ing).left().get()).getKey());
                    ByteBufCodecs.INT.encode(buf, ((TaggedFluidStackIngredient) ((Either) ing).left().get()).getAmount());
                });
            } catch (Exception exception) {
                buffer.setIndex(buffer.readerIndex(), write_index);
                buffer.writeArray(value.toArray(), (buf, ing) -> {
                    FluidStack.STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess(), ConnectionType.NEOFORGE), ((FluidStackIngredient) ((Either) ing).right().get()).getInputsRaw().getFirst());
                });
            }
        }
    };

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final StreamCodec<? super B, T8> codec8,
            final Function<C, T8> getter8,
            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B p_330310_) {
                T1 t1 = codec1.decode(p_330310_);
                T2 t2 = codec2.decode(p_330310_);
                T3 t3 = codec3.decode(p_330310_);
                T4 t4 = codec4.decode(p_330310_);
                T5 t5 = codec5.decode(p_330310_);
                T6 t6 = codec6.decode(p_330310_);
                T7 t7 = codec7.decode(p_330310_);
                T8 t8 = codec8.decode(p_330310_);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(B p_332052_, C p_331912_) {
                codec1.encode(p_332052_, getter1.apply(p_331912_));
                codec2.encode(p_332052_, getter2.apply(p_331912_));
                codec3.encode(p_332052_, getter3.apply(p_331912_));
                codec4.encode(p_332052_, getter4.apply(p_331912_));
                codec5.encode(p_332052_, getter5.apply(p_331912_));
                codec6.encode(p_332052_, getter6.apply(p_331912_));
                codec7.encode(p_332052_, getter7.apply(p_331912_));
                codec8.encode(p_332052_, getter8.apply(p_331912_));
            }
        };
    }

    @Override
    public MapCodec<RECIPE> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ITEM_CODEC.codec().listOf().fieldOf("inputItems").forGetter(RECIPE::inputItems),
                ITEM_CODEC.codec().listOf().fieldOf("outputItems").forGetter(RECIPE::outputItems),
                ANY_FLUID.codec().listOf().fieldOf("inputFluids").forGetter(RECIPE::inputFluids),
                ANY_FLUID.codec().listOf().fieldOf("outputFluids").forGetter(RECIPE::outputFluids),
                Codec.DOUBLE.fieldOf("timeModifier").forGetter(RECIPE::timeModifier),
                Codec.DOUBLE.fieldOf("powerModifier").forGetter(RECIPE::powerModifier),
                Codec.DOUBLE.fieldOf("radiationModifier").forGetter(RECIPE::radiationModifier),
                Codec.DOUBLE.fieldOf("rarityModifier").forGetter(RECIPE::rarityModifier)
        ).apply(instance, factory::create));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RECIPE> streamCodec() {
        return composite(
                LIST_INGREDIENT_STACK, RECIPE::inputItems,
                LIST_INGREDIENT_STACK, RECIPE::outputItems,
                LIST_FLUID_INGREDIENT_STACK, RECIPE::inputFluids,
                LIST_FLUID_INGREDIENT_STACK, RECIPE::outputFluids,
                ByteBufCodecs.DOUBLE, RECIPE::timeModifier,
                ByteBufCodecs.DOUBLE, RECIPE::powerModifier,
                ByteBufCodecs.DOUBLE, RECIPE::radiationModifier,
                ByteBufCodecs.DOUBLE, RECIPE::rarityModifier,
                factory::create
        );
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends NcRecipe> {
        RECIPE create(List<ItemStackIngredient> inputItems, List<ItemStackIngredient> outputItems,
                      List<Either<TaggedFluidStackIngredient, FluidStackIngredient>> inputFluids, List<Either<TaggedFluidStackIngredient, FluidStackIngredient>> outputFluids,
                      double timeMultiplier, double powerMultiplier, double radiationMultiplier, double rarityMultiplier);
    }
}