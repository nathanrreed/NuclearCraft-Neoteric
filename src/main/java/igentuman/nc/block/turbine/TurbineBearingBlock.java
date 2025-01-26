package igentuman.nc.block.turbine;

import igentuman.nc.block.MultiblockBlock;
import igentuman.nc.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TurbineBearingBlock extends MultiblockBlock {

    public TurbineBearingBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(TextUtils.applyFormat(Component.translatable("tooltip.nc.bearing.desc"), ChatFormatting.BLUE));
    }
}
