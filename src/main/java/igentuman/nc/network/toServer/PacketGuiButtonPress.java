package igentuman.nc.network.toServer;

import igentuman.nc.NuclearCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketGuiButtonPress(BlockPos tilePosition, int buttonId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketGuiButtonPress> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "packet_gui_button_press_to_server"));
    public static final StreamCodec<ByteBuf, PacketGuiButtonPress> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketGuiButtonPress::tilePosition, ByteBufCodecs.INT, PacketGuiButtonPress::buttonId, PacketGuiButtonPress::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}