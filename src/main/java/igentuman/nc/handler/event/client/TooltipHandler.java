package igentuman.nc.handler.event.client;

import igentuman.nc.handler.config.RadiationConfig;
import igentuman.nc.multiblock.fission.FissionBlocks;
import igentuman.nc.radiation.ItemRadiation;
import igentuman.nc.radiation.ItemShielding;
import igentuman.nc.radiation.RadiationCleaningItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Locale;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.handler.config.CommonConfig.ENERGY_STORAGE;
import static igentuman.nc.handler.config.FissionConfig.FISSION_CONFIG;
import static net.minecraft.world.item.Items.FILLED_MAP;
import static net.minecraft.world.item.Items.LIGHTNING_ROD;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TooltipHandler {
    private static ItemTooltipEvent processedEvent;

    public static void register(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(TooltipHandler::handle);
    }

    @SubscribeEvent
    public static void handle(ItemTooltipEvent event) {
        if (event.equals(processedEvent)) return;
        processedEvent = event;
        Item item = event.getItemStack().getItem();
        miscTooltips(event, event.getItemStack());
        if (RadiationConfig.RADIATION_CONFIG.ENABLED.get()) {
            addRadiationLevelTooltip(event, item);
            addShieldingTooltip(event, event.getItemStack());
            addRadiationCleaningEffect(event, event.getItemStack());
        }
        addModeratorTooltip(event, event.getItemStack());
    }

    private static void addModeratorTooltip(ItemTooltipEvent event, ItemStack itemStack) {
        for (Block block : FissionBlocks.moderators()) {
            if (itemStack.is(block.asItem())) {
                event.getToolTip().add(Component.translatable("tooltip.nc.moderator.desc", FISSION_CONFIG.MODERATOR_FE_MULTIPLIER.get(), FISSION_CONFIG.MODERATOR_HEAT_MULTIPLIER.get()).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    private static void miscTooltips(ItemTooltipEvent event, ItemStack itemStack) {
        if (itemStack.is(LIGHTNING_ROD) && ENERGY_STORAGE.LIGHTNING_ROD_CHARGE.get() > 0) {
            event.getToolTip().add(Component.translatable("tooltip.nc.lightning_rod_charge", ENERGY_STORAGE.LIGHTNING_ROD_CHARGE.get()).withStyle(ChatFormatting.GOLD));
        }

        CustomData data = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data.contains("is_nc_analyzed")) {
            event.getToolTip().add(Component.translatable("tooltip.nc.analyzed").withStyle(ChatFormatting.GOLD));
            if (itemStack.getItem().equals(FILLED_MAP)) {
                event.getToolTip().add(Component.translatable("tooltip.nc.use_in_leacher").withStyle(ChatFormatting.GOLD));
            }
        }
    }

    private static void addRadiationCleaningEffect(ItemTooltipEvent event, ItemStack itemStack) {
        long radiation = RadiationCleaningItems.byItem(itemStack.getItem());
        if (radiation == 0) return;
        ChatFormatting color = ChatFormatting.GREEN;
        event.getToolTip().add(Component.translatable("tooltip.nc.radiation_removal", format(((double) radiation) / 1000000000) + "Rad").withStyle(color));
    }

    private static void addShieldingTooltip(ItemTooltipEvent event, ItemStack item) {
        int shielding = ItemShielding.byItem(item.getItem());
        CustomData data = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if ((data.isEmpty() || !data.contains("rad_shielding")) && shielding == 0) return;
        ChatFormatting color = ChatFormatting.GOLD;
        if (!data.isEmpty() && data.contains("rad_shielding")) {
            shielding += data.copyTag().getInt("rad_shielding");
        }
        event.getToolTip().add(Component.translatable("tooltip.nc.rad_shielding", shielding).withStyle(color));
    }

    private static void addRadiationLevelTooltip(ItemTooltipEvent event, Item item) {
        double radiation = ItemRadiation.byItem(item);
        ChatFormatting color = ChatFormatting.GRAY;
        if (radiation > 0) {
            if (radiation > 0.0001) {
                color = ChatFormatting.YELLOW;
            }
            if (radiation > 0.001) {
                color = ChatFormatting.GOLD;
            }
            if (radiation > 0.1) {
                color = ChatFormatting.RED;
            }
            event.getToolTip().add(Component.translatable("tooltip.nc.radiation", format(radiation) + "Rad/s").withStyle(color));
        }
    }

    private static String format(Double radiation) {
        if (radiation >= 1) {
            return String.format(Locale.US, "%.0f", radiation) + " ";
        }
        if (radiation >= 0.000999) {
            return String.format(Locale.US, "%.0f", radiation * 1000) + " m";
        }
        if (radiation >= 0.000000999) {
            return String.format(Locale.US, "%.0f", radiation * 1000000) + " u";
        }
        return String.format(Locale.US, "%.0f", radiation * 1000000000) + " p";
    }
}