package igentuman.nc.setup;

import igentuman.nc.client.block.BatteryBlockItemDecorator;
import igentuman.nc.client.block.BatteryBlockLoader;
import igentuman.nc.client.block.fusion.FusionCoreRenderer;
import igentuman.nc.client.block.turbine.TurbineRotorRenderer;
import igentuman.nc.client.gui.FusionCoreScreen;
import igentuman.nc.client.gui.RedstoneDimmerScreen;
import igentuman.nc.client.gui.StorageContainerScreen;
import igentuman.nc.client.gui.fission.FissionControllerScreen;
import igentuman.nc.client.gui.fission.FissionPortScreen;
import igentuman.nc.client.gui.kugelblitz.ChamberPortScreen;
import igentuman.nc.client.gui.kugelblitz.ChamberTerminalScreen;
import igentuman.nc.client.gui.turbine.TurbineControllerScreen;
import igentuman.nc.client.gui.turbine.TurbinePortScreen;
import igentuman.nc.client.particle.FusionBeamParticle;
import igentuman.nc.client.particle.RadiationParticle;
import igentuman.nc.client.sound.SoundHandler;
import igentuman.nc.content.energy.BatteryBlocks;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.handler.event.client.*;
import igentuman.nc.radiation.client.ClientRadiationData;
import igentuman.nc.radiation.client.RadiationOverlay;
import igentuman.nc.radiation.client.WhiteNoiseOverlay;
import igentuman.nc.setup.registration.NCEnergyBlocks;
import igentuman.nc.setup.registration.NCFluids;
import igentuman.nc.setup.registration.NCProcessors;
import igentuman.nc.setup.registration.NCParticleTypes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.NuclearCraft.rl;
import static igentuman.nc.multiblock.fission.FissionReactor.FISSION_CONTROLLER_CONTAINER;
import static igentuman.nc.multiblock.fission.FissionReactor.FISSION_PORT_CONTAINER;
import static igentuman.nc.multiblock.fusion.FusionReactor.FUSION_BE;
import static igentuman.nc.multiblock.fusion.FusionReactor.FUSION_CORE_CONTAINER;
import static igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration.CHAMBER_PORT_CONTAINER;
import static igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration.CHAMBER_TERMINAL_CONTAINER;
import static igentuman.nc.multiblock.turbine.TurbineRegistration.*;
import static igentuman.nc.setup.registration.NCBlocks.REDSTONE_DIMMER_CONTAINER;
import static igentuman.nc.setup.registration.NCFluids.ALL_FLUID_ENTRIES;
import static igentuman.nc.setup.registration.NCFluids.FluidEntry.CLIENT_FLUIDTYPE_EXTENSIONS;
import static igentuman.nc.setup.registration.NCItems.GEIGER_COUNTER;
import static igentuman.nc.setup.registration.NCStorageBlocks.STORAGE_CONTAINER;
import static igentuman.nc.setup.registration.Registries.FLUIDS;
import static net.neoforged.bus.api.EventPriority.LOWEST;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerClientEventHandlers(FMLClientSetupEvent event) {
        ClientSetup.registerEventHandlers(event);
        NeoForge.EVENT_BUS.addListener(ClientSetup::registerGuiOverlays);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(STORAGE_CONTAINER.get(), StorageContainerScreen::new);
        event.register(FUSION_CORE_CONTAINER.get(), FusionCoreScreen::new);
        event.register(TURBINE_CONTROLLER_CONTAINER.get(), TurbineControllerScreen::new);
        event.register(TURBINE_PORT_CONTAINER.get(), TurbinePortScreen::new);
        event.register(FISSION_CONTROLLER_CONTAINER.get(), FissionControllerScreen::new);
        event.register(FISSION_PORT_CONTAINER.get(), FissionPortScreen::new);
        event.register(CHAMBER_PORT_CONTAINER.get(), ChamberPortScreen::new);
        event.register(CHAMBER_TERMINAL_CONTAINER.get(), ChamberTerminalScreen::new);
        event.register(REDSTONE_DIMMER_CONTAINER.get(), RedstoneDimmerScreen::new);

        for (String name : NCProcessors.PROCESSORS_CONTAINERS.keySet()) {
            event.register(NCProcessors.PROCESSORS_CONTAINERS.get(name).get(), Processors.all().get(name).getScreenConstructor());
        }
    }

    public static void init(FMLClientSetupEvent event) {
        //new BlackHoleShaderManager();
        event.enqueueWork(() -> {
            NeoForge.EVENT_BUS.addListener(LOWEST, SoundHandler::onTilePlaySound);
            BlockEntityRenderers.register(FUSION_BE.get("fusion_core").get(), FusionCoreRenderer::new);
            BlockEntityRenderers.register(TURBINE_BE.get("turbine_rotor_shaft").get(), TurbineRotorRenderer::new);
        });

        for (DeferredHolder<Fluid, ? extends Fluid> f : FLUIDS.getEntries())
            if (NCFluids.NC_GASES.containsKey(f.getId().getPath()))
                ItemBlockRenderTypes.setRenderLayer(f.get(), RenderType.translucent());

        event.enqueueWork(() -> {
            setPropertyOverride(GEIGER_COUNTER.get(), rl("radiation"), (stack, world, entity, seed) -> {
                if (entity instanceof Player) {
                    if (!((Player) entity).getInventory().contains(new ItemStack(GEIGER_COUNTER.get()))) return 0;
                    ClientRadiationData.setCurrentChunk(entity.chunkPosition().x, entity.chunkPosition().z);
                    return (int) ((float) ClientRadiationData.getCurrentWorldRadiation() / 400000);
                }
                return 0;
            });
        });
    }

    @SubscribeEvent
    public static void fluidLoad(RegisterClientExtensionsEvent event) {
        for (var fluid : CLIENT_FLUIDTYPE_EXTENSIONS) {
            event.registerFluidType(fluid, ALL_FLUID_ENTRIES.get(fluid.getName()).getStill().getFluidType());
        }
    }

    @SubscribeEvent
    public static void bucketColoring(RegisterColorHandlersEvent.Item event) {
        for (var fluid : CLIENT_FLUIDTYPE_EXTENSIONS) {
            event.register(((stack, tintIndex) -> tintIndex == 0 ? -1 : fluid.getTintColor()), ALL_FLUID_ENTRIES.get(fluid.getName()).getBucket().asItem());
        }
    }

    @SubscribeEvent
    public static void onModelRegistryEvent(ModelEvent.RegisterGeometryLoaders event) {
        event.register(BatteryBlockLoader.BATTERY_LOADER, new BatteryBlockLoader());
    }

    public static void setPropertyOverride(ItemLike itemProvider, ResourceLocation override, ItemPropertyFunction propertyGetter) { //TODO deprecated
        ItemProperties.register(itemProvider.asItem(), override, propertyGetter);
    }

    public static void registerGuiOverlays(CustomizeGuiOverlayEvent.DebugText event) { //TODO CHECK
        RadiationOverlay.RADIATION_BAR.render(event.getGuiGraphics(), event.getPartialTick());
        WhiteNoiseOverlay.WHITE_NOISE.render(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NCParticleTypes.RADIATION.get(), RadiationParticle.Factory::new);
        event.registerSpriteSet(NCParticleTypes.FUSION_BEAM.get(), FusionBeamParticle.Factory::new);
    }

    public static void setup() {
        IEventBus bus = NeoForge.EVENT_BUS;
    }

    public static void registerEventHandlers(FMLClientSetupEvent event) {
        InputEvents.register(event);
        ColorHandler.register(event);
        ServerLoad.register(event);
        RecipesUpdated.register(event);
        TagsUpdated.register(event);
        TooltipHandler.register(event);
        TickHandler.register(event);
        BlockOverlayHandler.register(event);
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        for (String name : BatteryBlocks.all().keySet()) {
            event.register(NCEnergyBlocks.BLOCK_ITEMS.get(name).get(), BatteryBlockItemDecorator.INSTANCE);
        }
    }
}
