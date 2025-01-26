package igentuman.nc.radiation.client;

import com.mojang.blaze3d.systems.RenderSystem;
import igentuman.nc.client.NcClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import static igentuman.nc.NuclearCraft.MODID;

public class WhiteNoiseOverlay {

    private static final ResourceLocation NOISE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/overlay/white_noise.png");

    public static final LayeredDraw.Layer WHITE_NOISE = (gui, deltaTracker) -> {
        Player pl = NcClient.tryGetClientPlayer();
        if (pl == null) return;
        int radiation = ClientRadiationData.getCurrentWorldRadiation();
        int level = radiation / 100000;
        if (level < 5) return;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, NOISE);
        assert Minecraft.getInstance().level != null;
        RandomSource rand = Minecraft.getInstance().level.random;
        for (int i = 0; i < rand.nextInt(level); i++) {
            int x1 = rand.nextInt(gui.guiWidth());
            int y1 = rand.nextInt(gui.guiHeight());
            int w = rand.nextInt(10);
            int h = rand.nextInt(10);
            gui.blit(NOISE, x1, y1, w, h, 1, 1, 12, 12);
        }
    };
}