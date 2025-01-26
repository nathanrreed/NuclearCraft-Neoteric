package igentuman.nc;

import igentuman.nc.handler.command.CommandNcPatrons;
import igentuman.nc.handler.command.CommandNcVeinCheck;
import igentuman.nc.handler.command.NCRadiationCommand;
import igentuman.nc.handler.command.StructureCommand;
import igentuman.nc.handler.config.*;
import igentuman.nc.handler.event.server.WorldEvents;
import igentuman.nc.network.PacketHandler;
import igentuman.nc.radiation.data.RadiationEvents;
import igentuman.nc.radiation.data.RadiationManager;
import igentuman.nc.setup.ClientSetup;
import igentuman.nc.setup.ModSetup;
import igentuman.nc.setup.Registration;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static igentuman.nc.util.FileExtractor.preFetchProcessorsConfig;
import static igentuman.nc.util.FileExtractor.unpackFilesFromFolderToConfig;

@Mod(NuclearCraft.MODID)
public class NuclearCraft {

    public static final Logger LOGGER = LogManager.getLogger();
    public boolean isNcBeStopped = false;
    public static final WorldEvents worldTickHandler = new WorldEvents();
    public static final String MODID = "nuclearcraft";
    public static NuclearCraft instance;
    private final PacketHandler packetHandler;

    public static void registerConfigs(ModContainer modContainer) {
        preFetchProcessorsConfig();
        unpackFilesFromFolderToConfig("data/nuclearcraft/fission_fuel", "NuclearCraft/fission_fuel");
        unpackFilesFromFolderToConfig("data/nuclearcraft/heat_sinks", "NuclearCraft/heat_sinks");
        modContainer.registerConfig(ModConfig.Type.COMMON, MaterialsConfig.spec, "NuclearCraft/materials.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, OreGenConfig.spec, "NuclearCraft/ore_generation.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.spec, "NuclearCraft/common.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, ProcessorsConfig.spec, "NuclearCraft/processors.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, FissionConfig.spec, "NuclearCraft/fission.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, FusionConfig.spec, "NuclearCraft/fusion.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, TurbineConfig.spec, "NuclearCraft/turbine.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, RadiationConfig.spec, "NuclearCraft/radiation.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, WorldConfig.spec, "NuclearCraft/world.toml");
    }


    public NuclearCraft(IEventBus MOD_BUS, ModContainer modContainer) {
        instance = this;
        registerConfigs(modContainer);
        packetHandler = new PacketHandler();

        Registration.init();
        //forceLoadConfig();
        NeoForge.EVENT_BUS.addListener(this::serverStopped);
        NeoForge.EVENT_BUS.addListener(this::serverStarted);
        NeoForge.EVENT_BUS.addListener(this::gameShuttingDownEvent);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        MOD_BUS.addListener(ModSetup::init);
        MOD_BUS.addListener(ClientSetup::registerScreens);

//        MOD_BUS.addListener(Capabilities::registerCapabilities); TODO

//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MOD_BUS.addListener(ClientSetup::init));
//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MOD_BUS.addListener(this::registerClientEventHandlers));
    }

    public static PacketHandler packetHandler() {
        return instance.packetHandler;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON)
            CommonConfig.setLoaded();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(CommandNcVeinCheck.register());
        event.getDispatcher().register(CommandNcPatrons.register());
        StructureCommand.register(event.getDispatcher());
        NCRadiationCommand.register(event.getDispatcher());
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void serverStopped(ServerStoppedEvent event) {
        NuclearCraft.instance.isNcBeStopped = true;
        //stop capability tracking
        RadiationEvents.stopTracking();
        for (ServerLevel level : event.getServer().getAllLevels()) {
            RadiationManager.clear(level);
        }
    }

    private void gameShuttingDownEvent(GameShuttingDownEvent event) {
        NuclearCraft.instance.isNcBeStopped = true;
    }

    private void serverStarted(ServerStartedEvent event) {
        NuclearCraft.instance.isNcBeStopped = false;
        RadiationEvents.startTracking();
    }

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
//        event.registerEntity(PlayerRadiation, EntityType.PLAYER, ); //TODO
//        event.register(WorldRadiation.class);
//        event.register(PlayerRadiation.class);
    }
}
