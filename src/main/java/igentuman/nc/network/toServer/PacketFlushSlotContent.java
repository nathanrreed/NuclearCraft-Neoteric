
package igentuman.nc.network.toServer;

import igentuman.nc.NuclearCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketFlushSlotContent(BlockPos tilePosition, int slotId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketFlushSlotContent> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "packet_flush_slot_content_to_server"));
    public static final StreamCodec<ByteBuf, PacketFlushSlotContent> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketFlushSlotContent::tilePosition, ByteBufCodecs.INT, PacketFlushSlotContent::slotId, PacketFlushSlotContent::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}