package igentuman.nc.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class ResearchPaperItem extends Item {
    public ResearchPaperItem(Properties pProperties) {
        super(pProperties);
    }

    public ResearchPaperItem(Properties props, CreativeModeTab group) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("vein")) {
            tooltipComponents.add(Component.translatable(tag.getString("vein")).withStyle(ChatFormatting.AQUA));
        }
        if (tag.contains("pos")) {
            BlockPos pos = BlockPos.of(tag.getLong("pos"));
            tooltipComponents.add(Component.translatable("tooltip.nc.chunk_position", pos.toShortString()).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(Component.translatable("tooltip.nc.use_in_leacher", pos.toShortString()).withStyle(ChatFormatting.GREEN));
        }
    }
}
