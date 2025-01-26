package igentuman.nc.item;

import igentuman.nc.util.CapabilityUtils;
import igentuman.nc.util.CustomEnergyStorage;
import igentuman.nc.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;

import static igentuman.nc.setup.registration.NCItems.*;

public class HEVItem extends ArmorItem {
    public HEVItem(Holder<ArmorMaterial> armorMaterials, ArmorItem.Type type, Properties hazmatProps) {
        super(armorMaterials, type, hazmatProps);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return Mth.hsvToRgb(Math.max(0.0F, getBarWidth(pStack) / (float) MAX_BAR_WIDTH) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    protected int getEnergyMaxStorage() {
        return 1000000;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CustomEnergyStorage energyStorage = getEnergy(stack);
        float chargeRatio = (float) energyStorage.getEnergyStored() / (float) getEnergyMaxStorage();
        return (int) Math.min(13, 13 * chargeRatio);
    }

//    @Override
//    public Object initCapabilities(ItemStack stack, CompoundTag nbt) {
//        return new ItemEnergyHandler<>(stack, getEnergyMaxStorage(), 5000, getEnergyMaxStorage() / 4);
//    }

    @Override
    public void inventoryTick(ItemStack st, Level level, Entity player, int slotIndex, boolean selectedIndex) {
        if (slotIndex <= EquipmentSlot.HEAD.getIndex() && slotIndex >= 0) {
            if (charged(st)) {
                if (st.getItem().equals(HEV_CHEST.get())) {
                    ((Player) player).addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1, 1, false, false));
                }
                if (st.getItem().equals(HEV_HELMET.get())) {
                    ((Player) player).addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 1, 1, false, false));
                }
                if (st.getItem().equals(HEV_PANTS.get())) {
                    ((Player) player).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 1, false, false));
                }
            }
        }
    }

    private boolean charged(ItemStack st) {
        return getEnergy(st).getEnergyStored() > 0;
    }

    public CustomEnergyStorage getEnergy(ItemStack stack) {
        return (CustomEnergyStorage) CapabilityUtils.getPresentCapability(stack, Capabilities.EnergyStorage.ITEM);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.nc.energy_stored", formatEnergy(getEnergy(stack).getEnergyStored()), formatEnergy(getEnergyMaxStorage())).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.translatable("tooltip.nc.hev.desc").withStyle(ChatFormatting.AQUA));
    }

    public String formatEnergy(int energy) {
        return TextUtils.scaledFormat(energy) + " FE";
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }
}