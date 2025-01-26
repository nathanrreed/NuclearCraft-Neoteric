package igentuman.nc.network.toServer;

import igentuman.nc.NuclearCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketSideConfigToggle(BlockPos tilePosition, int slotId, int direction) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketSideConfigToggle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "packet_side_config_toggle_to_server"));
    public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketSideConfigToggle::tilePosition, ByteBufCodecs.INT, PacketSideConfigToggle::slotId, ByteBufCodecs.INT, PacketSideConfigToggle::direction, PacketSideConfigToggle::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}