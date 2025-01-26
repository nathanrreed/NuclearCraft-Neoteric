package igentuman.nc.setup.registration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;

import static igentuman.nc.NuclearCraft.MODID;
import static net.neoforged.neoforge.common.Tags.Items.*;

public class Tags {
    public static final HashMap<String, TagKey<Item>> INGOTS_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> CHUNKS_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> GEMS_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> NUGGETS_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> PLATES_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> DUSTS_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> ORE_ITEM_TAGS = new HashMap<>();
    public static final HashMap<String, TagKey<Item>> BLOCK_ITEM_TAGS = new HashMap<>();
    public static final HashMap<String, TagKey<Block>> ORE_TAGS = new HashMap<>();
    public static final HashMap<String, TagKey<Block>> BLOCK_TAGS = new HashMap<>();
    public static final HashMap<String, TagKey<Fluid>> GASES_TAG = new HashMap<>();
    public static final HashMap<String, TagKey<Fluid>> LIQUIDS_TAG = new HashMap<>();
    public static final TagKey<Item> PLATE_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "plates"));
    public static final TagKey<Item> PARTS_TAG = itemTag("parts");
    public static final TagKey<Item> ISOTOPE_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "isotopes"));
    public static final TagKey<Item> NC_ISOTOPE_TAG = itemTag("isotopes");
    public static final TagKey<Item> NC_FUEL_TAG = itemTag("reactor_fuel");
    public static final TagKey<Item> NC_DEPLETED_FUEL_TAG = itemTag("reactor_fuel");

    public static final TagKey<Block> MINEABLE_WITH_PAXEL = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "mineable/paxel"));
    public static final TagKey<Block> INCORRECT_FOR_TOUGH = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "incorrect_for_tough_tool"));
    public static final TagKey<Block> INCORRECT_FOR_THORIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "incorrect_for_thorium_tool"));
    public static final TagKey<Block> INCORRECT_FOR_QNP = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "incorrect_for_qnp_tool"));
//    public static TagKey<Item> NC_FUELS_TAG = itemTag("reactor_fuel");
//    public static TagKey<Item> NC_FUELS_TAG = itemTag("reactor_fuel");

    public static TagKey<Block> blockTag(String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    public static TagKey<Item> itemTag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    public static void addIngotTag(String name) {
        INGOTS_TAG.put(name, forgeIngot(name));
    }

    public static void addNuggetTag(String name) {
        NUGGETS_TAG.put(name, forgeNugget(name));
    }

    public static void addPlateTag(String name) {
        PLATES_TAG.put(name, forgePlate(name));
    }

    public static void addOreTag(String name) {
        ORE_ITEM_TAGS.put(name, forgeOre(name));
    }

    public static void addDustTag(String name) {
        DUSTS_TAG.put(name, forgeDust(name));
    }

    public static void addGemTag(String name) {
        GEMS_TAG.put(name, forgeGem(name));
    }

    public static void addChunkTag(String name) {
        CHUNKS_TAG.put(name, forgeChunk(name));
    }

    public static TagKey<Item> forgeIngot(String name) {
        return ItemTags.create(INGOTS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeGem(String name) {
        return ItemTags.create(GEMS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeNugget(String name) {
        return ItemTags.create(NUGGETS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeBlock(String name) {
        return ItemTags.create(STORAGE_BLOCKS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeOre(String name) {
        return ItemTags.create(ORES.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeBucket(String name) {
        return ItemTags.create(BUCKETS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeChunk(String name) {
        return ItemTags.create(RAW_MATERIALS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgeDust(String name) {
        return ItemTags.create(DUSTS.location().withSuffix("/" + name));
    }

    public static TagKey<Item> forgePlate(String name) {
        return ItemTags.create(ResourceLocation.parse("c:plates/" + name));
    }

    public static TagKey<Item> forgeDye(String name) {
        return ItemTags.create(DYES.location().withSuffix("/" + name));
    }
}