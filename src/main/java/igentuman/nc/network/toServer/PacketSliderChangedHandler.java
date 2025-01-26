package igentuman.nc.network.toServer;

import igentuman.nc.block.entity.NuclearCraftBE;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSliderChangedHandler {
    public static void handle(PacketSliderChanged data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) {
                return;
            }

            NuclearCraftBE be = (NuclearCraftBE) player.level().getBlockEntity(data.tilePosition());
            if (be != null) {
                be.handleSliderUpdate(data.buttonId(), data.ratio());
            }
        });
    }
}
