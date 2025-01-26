package igentuman.nc.item;

import igentuman.nc.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.List;

public class ProcessorBlockItem extends BlockItem {
    public ProcessorBlockItem(Block pBlock, Properties props) {
        super(pBlock, new Properties());
    }

    @Override
    public boolean isRepairable(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("energy")) {
            tooltipComponents.add(Component.translatable("tooltip.nc.content_saved").withStyle(ChatFormatting.GRAY));
        }
        if (asItem().toString().contains("empty") || this.asItem().equals(Items.AIR)) return;
        tooltipComponents.add(TextUtils.applyFormat(Component.translatable("processor.description." + this.toString().replace("nuclearcraft:", "")), ChatFormatting.AQUA));
    }
}