package igentuman.nc.network.toServer;

import igentuman.nc.block.entity.fission.FissionControllerBE;
import igentuman.nc.block.entity.fission.FissionPortBE;
import igentuman.nc.block.entity.fusion.FusionCoreBE;
import igentuman.nc.block.entity.fusion.FusionCoreProxyBE;
import igentuman.nc.block.entity.processor.NCProcessorBE;
import igentuman.nc.client.gui.element.button.Button;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketGuiButtonPressHandler {
    public static void handle(PacketGuiButtonPress data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            BlockEntity be = player.level().getBlockEntity(data.tilePosition());
            switch (data.buttonId()) {
                case Button.RedstoneConfig.BTN_ID:
                    if (!(be instanceof NCProcessorBE<?> processor)) {
                        return;
                    }
                    processor.toggleRedstoneMode();
                    break;
                case Button.ReactorMode.BTN_ID:
                    if (!(be instanceof FissionControllerBE<?> port)) {
                        return;
                    }
                    port.toggleMode();
                    break;
                case Button.ReactorPortRedstoneModeButton.BTN_ID:
                    if (!(be instanceof FissionPortBE port)) {
                        return;
                    }
                    port.toggleRedstoneMode();
                    break;
                case Button.FusionReactorRedstoneModeButton.BTN_ID:
                    if (be instanceof FusionCoreBE<?> port) {
                        port.toggleRedstoneMode();
                    }
                    if (be instanceof FusionCoreProxyBE port) {
                        port.toggleRedstoneMode();
                    }
                    break;
            }
        });
    }
}
