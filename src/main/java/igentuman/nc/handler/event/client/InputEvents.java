package igentuman.nc.handler.event.client;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import static com.mojang.blaze3d.platform.InputConstants.*;

public class InputEvents {
    public static boolean DESCRIPTIONS_SHOW = false;
    public static boolean SHIFT_PRESSED = false;

    public static void register(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(InputEvents::onScreenKeyPressed);
        NeoForge.EVENT_BUS.addListener(InputEvents::onScreenKeyReleased);
    }

    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
        if (event.getKeyCode() == KEY_LSHIFT || event.getKeyCode() == KEY_RSHIFT) {
            SHIFT_PRESSED = true;
        }
    }

    public static void onScreenKeyReleased(ScreenEvent.KeyReleased.Post event) {
        if (event.getKeyCode() == KEY_LSHIFT || event.getKeyCode() == KEY_RSHIFT) {
            SHIFT_PRESSED = false;
        }

        if (event.getKeyCode() == KEY_N && event.getModifiers() == MOD_CONTROL) {
            DESCRIPTIONS_SHOW = !DESCRIPTIONS_SHOW;
        }
    }
}