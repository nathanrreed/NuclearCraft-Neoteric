package igentuman.nc.setup.registration;

import igentuman.nc.block.*;
import igentuman.nc.block.entity.RedstoneDimmerBE;
import igentuman.nc.container.RedstoneDImmerContainer;
import igentuman.nc.content.Electromagnets;
import igentuman.nc.content.RFAmplifier;
import igentuman.nc.content.materials.Blocks;
import igentuman.nc.content.materials.Materials;
import igentuman.nc.content.materials.Ores;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static igentuman.nc.setup.registration.NCItems.*;
import static igentuman.nc.setup.registration.Registries.*;
import static igentuman.nc.setup.registration.Tags.*;
import static net.neoforged.neoforge.common.Tags.Blocks.ORES;
import static net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS;

public class NCBlocks {
    public static final BlockBehaviour.Properties ORE_BLOCK_PROPERTIES = BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(2f).requiresCorrectToolForDrops();
    public static final Item.Properties BLOCK_ITEM_PROPERTIES = new Item.Properties();
    public static final BlockBehaviour.Properties NC_BLOCKS_PROPERTIES = BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(2f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties ORE_DEEPSLATE_BLOCK_PROPERTIES = BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(4f).requiresCorrectToolForDrops();
    public static HashMap<String, DeferredBlock<Block>> ORE_BLOCKS = new HashMap<>();
    public static HashMap<String, DeferredBlock<Block>> NC_BLOCKS = new HashMap<>();
    public static HashMap<String, DeferredBlock<Block>> NC_RF_AMPLIFIERS = new HashMap<>();
    public static HashMap<String, DeferredBlock<Block>> NC_ELECTROMAGNETS = new HashMap<>();
    public static HashMap<String, DeferredBlock<Block>> MULTI_BLOCKS = new HashMap<>();
    public static HashMap<String, DeferredBlock<Block>> NC_MATERIAL_BLOCKS = new HashMap<>();
    public static final Item.Properties ORE_ITEM_PROPERTIES = new Item.Properties();
    public static final Item.Properties MULTIBLOCK_ITEM_PROPERTIES = new Item.Properties();
    public static final DeferredBlock<Block> PORTAL_BLOCK = BLOCKS.register("portal", PortalBlock::new);
    public static final DeferredBlock<Block> REDSTONE_DIMMER_BLOCK = BLOCKS.register("redstone_dimmer", () -> new RedstoneDimmerBlock());
    public static final DeferredItem<Item> REDSTONE_DIMMER_ITEM_BLOCK = fromBlock(REDSTONE_DIMMER_BLOCK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneDimmerBE>> REDSTONE_DIMMER_BE = BLOCK_ENTITIES.register("redstone_dimmer",
            () -> BlockEntityType.Builder.of(RedstoneDimmerBE::new, REDSTONE_DIMMER_BLOCK.get()).build(null));
    public static final DeferredBlock<Block> MUSHROOM_BLOCK = BLOCKS.register("glowing_mushroom", () -> new GlowingMushroomBlock(
            BlockBehaviour.Properties.of().sound(SoundType.GRASS).noCollission().instabreak().randomTicks().lightLevel($ -> 10)
    ));
    public static final DeferredHolder<MenuType<?>, MenuType<RedstoneDImmerContainer>> REDSTONE_DIMMER_CONTAINER = CONTAINERS.register("redstone_dimmer",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new RedstoneDImmerContainer(windowId, data.readBlockPos(), inv)));
    public static final DeferredItem<Item> MUSHROOM_ITEM = fromBlock(MUSHROOM_BLOCK);
    public static final DeferredItem<Item> PORTAL_ITEM = fromBlock(PORTAL_BLOCK);
    public static TagKey<Block> DECAY_GEN_BLOCK = blockTag("decay_gen_block");

    public static void init() {
        registerOres();
        registerBlocks();
        registerMagnets();
        registerAmplifiers();
    }

    private static void registerOres() {
        for (String name : Ores.all().keySet()) {
            ORE_TAGS.put(name, BlockTags.create(ORES.location().withSuffix("/" + name)));
            addOreTag(name);
            if (Materials.ores().get(name).normal_ore) {
                ORE_BLOCKS.put(name, BLOCKS.register(name + "_ore", () -> new Block(ORE_BLOCK_PROPERTIES)));
                ORE_BLOCK_ITEMS.put(name, fromOreBlock(ORE_BLOCKS.get(name)));
                ALL_NC_ITEMS.put("ore_" + name, ORE_BLOCK_ITEMS.get(name));
            }
            if (Materials.ores().get(name).deepslate_ore) {
                ORE_BLOCKS.put(name + "_deepslate", BLOCKS.register(name + "_deepslate_ore", () -> new Block(ORE_DEEPSLATE_BLOCK_PROPERTIES)));
                ORE_BLOCK_ITEMS.put(name + "_deepslate", fromOreBlock(ORE_BLOCKS.get(name + "_deepslate")));
                ALL_NC_ITEMS.put("ore_" + name + "_deepslate", ORE_BLOCK_ITEMS.get(name + "_deepslate"));
            }
            if (Materials.ores().get(name).nether_ore) {
                ORE_BLOCKS.put(name + "_nether", BLOCKS.register(name + "_nether_ore", () -> new Block(ORE_BLOCK_PROPERTIES)));
                ORE_BLOCK_ITEMS.put(name + "_nether", fromOreBlock(ORE_BLOCKS.get(name + "_nether")));
                ALL_NC_ITEMS.put("ore_" + name + "_nether", ORE_BLOCK_ITEMS.get(name + "_nether"));
            }
            if (Materials.ores().get(name).end_ore) {
                ORE_BLOCKS.put(name + "_end", BLOCKS.register(name + "_end_ore", () -> new Block(ORE_BLOCK_PROPERTIES)));
                ORE_BLOCK_ITEMS.put(name + "_end", fromOreBlock(ORE_BLOCKS.get(name + "_end")));
                ALL_NC_ITEMS.put("ore_" + name + "_end", ORE_BLOCK_ITEMS.get(name + "_end"));
            }
        }
    }

    private static void registerMagnets() {
        for (String name : Electromagnets.all().keySet()) {
            NC_ELECTROMAGNETS.put(name, BLOCKS.register(name, () -> new ElectromagnetBlock(NC_BLOCKS_PROPERTIES)));
            NC_ELECTROMAGNETS.put(name + "_slope", BLOCKS.register(name + "_slope", () -> new ElectromagnetSlopeBlock(NC_BLOCKS_PROPERTIES)));
            NC_ELECTROMAGNETS_ITEMS.put(name, fromBlock(NC_ELECTROMAGNETS.get(name)));
            NC_ELECTROMAGNETS_ITEMS.put(name + "_slope", fromBlock(NC_ELECTROMAGNETS.get(name + "_slope")));
            ALL_NC_ITEMS.put(name, NC_ELECTROMAGNETS_ITEMS.get(name));
            ALL_NC_ITEMS.put(name + "_slope", NC_ELECTROMAGNETS_ITEMS.get(name + "_slope"));
        }
    }

    private static void registerAmplifiers() {
        for (String name : RFAmplifier.all().keySet()) {
            NC_RF_AMPLIFIERS.put(name, BLOCKS.register(name, () -> new RFAmplifierBlock(NC_BLOCKS_PROPERTIES)));
            NC_RF_AMPLIFIERS_ITEMS.put(name, fromBlock(NC_RF_AMPLIFIERS.get(name)));
            ALL_NC_ITEMS.put(name, NC_RF_AMPLIFIERS_ITEMS.get(name));
        }
    }

    private static void registerBlocks() {
        for (String name : Blocks.get().all().keySet()) {
            BLOCK_TAGS.put(name, BlockTags.create(STORAGE_BLOCKS.location().withSuffix("/" + name)));
            BLOCK_ITEM_TAGS.put(name, ItemTags.create(Items.STORAGE_BLOCKS.location().withSuffix("/" + name)));
            NC_MATERIAL_BLOCKS.put(name, BLOCKS.register(name + "_block", () -> new Block(NC_BLOCKS_PROPERTIES)));
            NC_BLOCKS_ITEMS.put(name, fromBlock(NC_MATERIAL_BLOCKS.get(name)));
            ALL_NC_ITEMS.put(name + "_block", NC_BLOCKS_ITEMS.get(name));
        }
        ALL_NC_ITEMS.put("glowing_mushroom", NC_BLOCKS_ITEMS.get("glowing_mushroom"));
    }

    public static <B extends Block> DeferredItem<Item> fromOreBlock(DeferredBlock<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ORE_ITEM_PROPERTIES));
    }

    public static <B extends Block> DeferredItem<Item> fromBlock(DeferredBlock<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), BLOCK_ITEM_PROPERTIES));
    }

    public static <B extends Block> DeferredItem<Item> fromMultiblock(DeferredBlock<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), MULTIBLOCK_ITEM_PROPERTIES));
    }

    public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
        public static final Collection<BlockEntry<?>> ALL_ENTRIES = new ArrayList<>();

        private final DeferredBlock<T> regObject;
        private final Supplier<BlockBehaviour.Properties> properties;

        public static BlockEntry<FenceBlock> fence(String name, Supplier<BlockBehaviour.Properties> props) {
            return new BlockEntry<>(name, props, FenceBlock::new);
        }

        public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
            this.properties = properties;
            this.regObject = BLOCKS.register(name, () -> make.apply(properties.get()));
            ALL_ENTRIES.add(this);
        }

        public BlockEntry(T existing) {
            this.properties = () -> BlockBehaviour.Properties.ofFullCopy(existing);
            this.regObject = DeferredBlock.createBlock(BuiltInRegistries.BLOCK.getKey(existing));
        }

        @SuppressWarnings("unchecked")
        public BlockEntry(BlockEntry<? extends T> toCopy) {
            this.properties = toCopy.properties;
            this.regObject = (DeferredBlock<T>) toCopy.regObject;
        }

        @Override
        public T get() {
            return regObject.get();
        }

        public BlockState defaultBlockState() {
            return get().defaultBlockState();
        }

        public ResourceLocation getId() {
            return regObject.getKey().location();
        }

        public BlockBehaviour.Properties getProperties() {
            return properties.get();
        }

        @Nonnull
        @Override
        public Item asItem() {
            return get().asItem();
        }
    }
}