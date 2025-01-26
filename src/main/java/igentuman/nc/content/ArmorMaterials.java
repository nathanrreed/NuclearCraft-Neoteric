package igentuman.nc.content;

import igentuman.nc.NuclearCraft;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static igentuman.nc.setup.registration.NCItems.NC_PARTS;

public class ArmorMaterials {

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final HashMap<String, Integer> durabilityMultiplier = new HashMap<>();

    public static final Holder<ArmorMaterial> HAZMAT = register("hazmat", 5, List.of(1, 2, 3, 1, 3), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(NC_PARTS.get("bioplastic").get()));
    public static final Holder<ArmorMaterial> TOUGH = register("tough", 33, List.of(3, 6, 8, 3, 11), 15, SoundEvents.ARMOR_EQUIP_DIAMOND, 3.5F, 0.2F, () -> Ingredient.of(NC_PARTS.get("tough_alloy").get()));
    public static final Holder<ArmorMaterial> HEV = register("hev", 37, List.of(3, 5, 7, 3, 11), 25, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.3F, () -> Ingredient.of(NC_PARTS.get("dps").get()));

    public static Holder<ArmorMaterial> register(String name, int pDurabilityMultiplier, List<Integer> pSlotProtections, int pEnchantmentValue, Holder<SoundEvent> pSound, float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngredient) {
        durabilityMultiplier.put(name, pDurabilityMultiplier);

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, name), new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, pSlotProtections.get(0));
            map.put(ArmorItem.Type.LEGGINGS, pSlotProtections.get(1));
            map.put(ArmorItem.Type.CHESTPLATE, pSlotProtections.get(2));
            map.put(ArmorItem.Type.HELMET, pSlotProtections.get(3));
            map.put(ArmorItem.Type.BODY, pSlotProtections.get(4));
        }), pEnchantmentValue, SoundEvents.ARMOR_EQUIP_LEATHER, pRepairIngredient, List.of(), pToughness, pKnockbackResistance));
    }

    public static int getDurabilityForSlot(EquipmentSlot pSlot, String name) {
        return HEALTH_PER_SLOT[pSlot.getIndex()] * durabilityMultiplier.get(name);
    }
}