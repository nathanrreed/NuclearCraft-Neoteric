package igentuman.nc.network.toClient;

import igentuman.nc.NuclearCraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketPlayerRadiationData(long playerRadiation) implements CustomPacketPayload {
    public static final Type<PacketPlayerRadiationData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "player_radiation_data_to_client"));
    public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, PacketPlayerRadiationData::playerRadiation, PacketPlayerRadiationData::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}