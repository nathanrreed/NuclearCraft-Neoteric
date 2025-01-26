package igentuman.nc.network.toServer;

import igentuman.nc.block.entity.processor.NCProcessorBE;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSideConfigToggleHandler {
    public static void handle(PacketSideConfigToggle data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(data.tilePosition());
            if (!(be instanceof NCProcessorBE processor)) {
                return;
            }
            processor.toggleSideConfig(data.slotId(), data.direction());
        });
    }
}