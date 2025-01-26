package igentuman.nc.item;

import igentuman.nc.content.storage.BarrelBlocks;
import igentuman.nc.util.CapabilityUtils;
import igentuman.nc.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import java.util.List;

public class BarrelBlockItem extends BlockItem {

    public BarrelBlockItem(Block pBlock, Properties props) {
        super(pBlock, new Properties().stacksTo(1));
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

//
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
//        return new FluidHandlerItemStack(stack, getCapacity());
//    }

    public int getCapacity() {
        return BarrelBlocks.all().get(code()).config().getCapacity();
    }

    public IFluidHandlerItem getFluid(ItemStack stack) {
        return (IFluidHandlerItem) CapabilityUtils.getPresentCapability(stack, Capabilities.FluidHandler.ITEM);
    }

    public String code() {
        return asItem().toString();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int storage = BarrelBlocks.all().get(code()).config().getCapacity();
        FluidStack fluid = getFluid(stack).getFluidInTank(0);
        if (fluid == null || FluidStack.isSameFluidSameComponents(fluid, FluidStack.EMPTY)) {
            tooltipComponents.add(Component.translatable("tooltip.nc.liquid_empty", formatLiquid(storage)).withStyle(ChatFormatting.BLUE));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.nc.liquid_stored", Component.translatable(fluid.getDescriptionId()).getString(), formatLiquid(fluid.getAmount()), formatLiquid(storage)).withStyle(ChatFormatting.BLUE));
        }
        tooltipComponents.add(Component.translatable("tooltip.nc.use_multitool").withStyle(ChatFormatting.YELLOW));
    }

    public String formatLiquid(int val) {
        return TextUtils.numberFormat(val / 1000) + " B";
    }
}