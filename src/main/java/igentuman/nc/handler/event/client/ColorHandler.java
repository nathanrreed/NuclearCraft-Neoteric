package igentuman.nc.handler.event.client;

import igentuman.nc.setup.registration.NCFluids;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.setup.registration.NCFluids.ALL_FLUID_ENTRIES;
import static igentuman.nc.setup.registration.NCFluids.NC_MATERIALS;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ColorHandler {
    public static void register(FMLClientSetupEvent event) {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        bus.addListener(ColorHandler::registerItemColorHandlers);
        bus.addListener(ColorHandler::registerBlockColorHandlers);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        registerBucketColorHandler(event);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for (String gas : NCFluids.NC_GASES.keySet()) {
            ItemBlockRenderTypes.setRenderLayer(ALL_FLUID_ENTRIES.get(gas).getFlowing(), RenderType.translucent());
        }

        for (String fluid : NC_MATERIALS.keySet()) {
            if (fluid.contains("molten")) return;
            ItemBlockRenderTypes.setRenderLayer(NC_MATERIALS.get(fluid).getStill(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(NC_MATERIALS.get(fluid).getFlowing(), RenderType.translucent());
        }
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        for (String name : NCFluids.NC_GASES.keySet()) {

        }
    }

    private static final ItemColor BUCKET_ITEM_COLOR = new DynamicFluidContainerModel.Colors();

    public static void registerBucketColorHandler(RegisterColorHandlersEvent.Item event) {
        for (String name : NC_MATERIALS.keySet()) {
            event.register(BUCKET_ITEM_COLOR, NC_MATERIALS.get(name).getBucket());
        }
        for (String name : NCFluids.NC_GASES.keySet()) {
            event.register(BUCKET_ITEM_COLOR, NCFluids.NC_GASES.get(name).getBucket());
        }
    }
}