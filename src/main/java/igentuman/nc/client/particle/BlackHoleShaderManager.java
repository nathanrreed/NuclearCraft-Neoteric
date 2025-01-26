package igentuman.nc.client.particle;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;
import java.util.function.Supplier;

public class BlackHoleShaderManager {

    public BlackHoleShaderManager() {
        NeoForge.EVENT_BUS.register(this);
    }

    private static final ShaderTracker tracker = new ShaderTracker();

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(ShaderLoader.getBlackHoleShader(), tracker::setInstance);
    }

    static class ShaderTracker implements Supplier<ShaderInstance> {

        private ShaderInstance instance;

        private ShaderTracker() {
        }

        private void setInstance(ShaderInstance instance) {
            this.instance = instance;
        }

        @Override
        public ShaderInstance get() {
            return instance;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();

        // Check if the player is near the black hole
        double playerX = mc.player.getX();
        double playerY = mc.player.getY();
        double playerZ = mc.player.getZ();

        double blackHoleX = 110;
        double blackHoleY = -60;
        double blackHoleZ = 102;
        ShaderInstance shader = ShaderLoader.getBlackHoleShader();
        double distance = Math.sqrt(Math.pow(playerX - blackHoleX, 2)
                + Math.pow(playerY - blackHoleY, 2)
                + Math.pow(playerZ - blackHoleZ, 2));
        Uniform radiusUniform = shader.getUniform("BlackholeRadius");
        Uniform distortionUniform = shader.getUniform("DistortionAmount");
        Uniform time = shader.getUniform("Time");
        if (radiusUniform != null) {
            radiusUniform.set(1.3f);
        }
        if (distortionUniform != null) {
            distortionUniform.set(0.5f);
        }
        if (time != null) {
            time.set((float) (mc.level.getGameTime() / 20));
        }

        shader.apply();
        if (distance < 15) {
            RenderSystem.setShader(() -> shader);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionShader);
        }
    }
}
