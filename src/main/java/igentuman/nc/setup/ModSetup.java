package igentuman.nc.setup;

import igentuman.nc.NuclearCraft;
import igentuman.nc.radiation.data.RadiationEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import static igentuman.nc.NuclearCraft.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModSetup {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(RadiationEvents::onPlayerCloned);
//        bus.addGenericListener(Entity.class, RadiationEvents::attachPlayerRadiation); TODO find out how to add
//        bus.addGenericListener(Level.class, RadiationEvents::attachWorldRadiation);
//        bus.register(NuclearCraft.worldTickHandler);
//        bus.register(new RadiationEvents());
    }

    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //Dimensions.register();
            //CapabilityRegistration.register(event);

        });
    }
}
