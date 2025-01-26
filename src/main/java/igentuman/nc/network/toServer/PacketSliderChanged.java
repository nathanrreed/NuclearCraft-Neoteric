package igentuman.nc.network.toServer;

import igentuman.nc.NuclearCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketSliderChanged(BlockPos tilePosition, int ratio, int buttonId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketSliderChanged> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "packet_slider_changed_to_server"));
    public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketSliderChanged::tilePosition, ByteBufCodecs.INT, PacketSliderChanged::ratio, ByteBufCodecs.INT, PacketSliderChanged::buttonId, PacketSliderChanged::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}