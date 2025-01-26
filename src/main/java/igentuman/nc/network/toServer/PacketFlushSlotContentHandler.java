package igentuman.nc.network.toServer;

import igentuman.nc.block.entity.processor.NCProcessorBE;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketFlushSlotContentHandler {
    public static void handle(PacketFlushSlotContent data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            BlockEntity be = player.level().getBlockEntity(data.tilePosition());
            if (!(be instanceof NCProcessorBE<?> ncBe)) {
                return;
            }
            ncBe.voidFluidSlot(data.slotId());
        });
    }
}
