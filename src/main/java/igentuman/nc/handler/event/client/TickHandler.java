package igentuman.nc.handler.event.client;

import igentuman.nc.client.sound.GeigerSound;
import igentuman.nc.client.sound.SoundHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public class TickHandler {

    public static String currentScreenCode = "";

    public static final Minecraft minecraft = Minecraft.getInstance();

    public static void register(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(TickHandler::onTick);
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        tickStart();
    }

    protected static GeigerSound geigerSound;

    public static void tickStart() {
        if (minecraft.player == null) {
            return;
        }
        GeigerSound toPlay = GeigerSound.create(minecraft.player);
        if (toPlay != null && (geigerSound == null || geigerSound.radiationLevel != toPlay.radiationLevel)) {
            if (geigerSound != null) {
                SoundHandler.stopSound(geigerSound);
            }
            geigerSound = toPlay;
            SoundHandler.playSound(geigerSound);
        }

        if (toPlay == null && geigerSound != null) {
            SoundHandler.stopSound(geigerSound);
            geigerSound = null;
        }
    }
}