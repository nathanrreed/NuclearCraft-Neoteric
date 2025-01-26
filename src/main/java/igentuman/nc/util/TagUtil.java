package igentuman.nc.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static igentuman.nc.handler.config.MaterialsConfig.MATERIAL_PRODUCTS;

public class TagUtil {
    public static Fluid getFirstMatchingFluidByTag(String key, HolderLookup.Provider lookUpProvider) {
        if (key.contains(":")) {
            String[] parts = key.split(":");
            if (!Objects.equals(parts[0], "c")) {
                FluidStack fluid = getFluidByName(key.replace("/", "_"), lookUpProvider);
                if (!fluid.isEmpty()) {
                    return fluid.getFluid();
                }
                fluid = getFluidByName(key, lookUpProvider);
                if (!fluid.isEmpty()) {
                    return fluid.getFluid();
                }
            }
            key = parts[1];
        }

        for (String mod : MATERIAL_PRODUCTS.MODS_PRIORITY.get()) {
            FluidStack fluid = getFluidByName(mod + ":" + key, lookUpProvider);
            if (!fluid.isEmpty()) {
                return fluid.getFluid();
            }
            fluid = getFluidByName(mod + ":" + key.replace("/", "_"), lookUpProvider);
            if (!fluid.isEmpty()) {
                return fluid.getFluid();
            }
        }
        return FluidStack.EMPTY.getFluid();
    }

    public static FluidStack getFluidByName(String name, HolderLookup.Provider lookUpProvider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("FluidName", name);
        tag.putInt("Amount", 1);

        return FluidStack.parseOptional(lookUpProvider, tag);
    }

    public static List<Block> getBlocksByTagKey(String key) {
        return getBlocksByTagKey(ResourceLocation.parse(key));
    }

    public static List<Block> getBlocksByTagKey(ResourceLocation key) {
        return Arrays.stream(Ingredient.of(ItemTags.create(key)).getItems()).map(stack -> Block.byItem(stack.getItem())).toList();
    }

    public static Block getSingleBlockByTagKey(String key) {
        for (String mod : MATERIAL_PRODUCTS.MODS_PRIORITY.get()) {
            for (Block holder : getBlocksByTagKey(key)) {
                if (holder.getDescriptionId().contains(mod)) {
                    return holder;
                }
            }
        }
        return getBlocksByTagKey(key).getFirst();
    }

    public static List<Item> getItemsByTagKey(String key) {
        return getItemsByTagKey(ResourceLocation.parse(key));
    }

    public static List<Item> getItemsByTagKey(ResourceLocation key) {
        return Arrays.stream(Ingredient.of(ItemTags.create(key)).getItems()).map(ItemStack::getItem).toList();
    }

//    public static <TYPE> TagManager manager(Registry<TYPE> registry) {
//        TagManager tags = registry.tags();
//        if (tags == null) {
//            throw new IllegalStateException("Expected " + registry.getRegistryName() + " to have tags.");
//        }
//        return tags;
//    }
//
//    public static <TYPE> TagKey<TYPE> tag(Registry<TYPE> registry, TagKey<TYPE> key) {
//        return manager(registry).getTag(key);
//    }
//
//    public static <TYPE> TagKey<TYPE> createKey(Registry<TYPE> registry, ResourceLocation tag) {
//        return manager(registry).createTagKey(tag);
//    }
//
//    public static <TYPE> Set<TagKey<TYPE>> tags(Registry<TYPE> registry, TYPE element) {
//        return tags(manager(registry), element);
//    }
//
//    public static <TYPE> Set<TagKey<TYPE>> tags(ITagManager<TYPE> tagManager, TYPE element) {
//        return tagsStream(tagManager, element).collect(Collectors.toSet());
//    }
//
//    public static <TYPE> Stream<TagKey<TYPE>> tagsStream(Registry<TYPE> registry, TYPE element) {
//        return tagsStream(manager(registry), element);
//    }
//
//    public static <TYPE> Stream<TagKey<TYPE>> tagsStream(ITagManager<TYPE> tagManager, TYPE element) {
//        return tagManager.getReverseTag(element)
//                .map(IReverseTag::getTagKeys)
//                .orElse(Stream.empty());
//    }
//
//    public static <TYPE> Set<ResourceLocation> tagNames(Registry<TYPE> registry, TYPE element) {
//        return tagNames(manager(registry), element);
//    }
//
//    public static <TYPE> Set<ResourceLocation> tagNames(ITagManager<TYPE> tagManager, TYPE element) {
//        return tagNames(tagsStream(tagManager, element));
//    }
//
//    public static Set<ResourceLocation> tagNames(Stream<? extends TagKey<?>> stream) {
//        return stream.map(TagKey::location)
//                .collect(Collectors.toUnmodifiableSet());
//    }
}
