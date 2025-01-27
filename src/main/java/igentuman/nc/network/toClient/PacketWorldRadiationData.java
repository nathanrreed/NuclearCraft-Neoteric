package igentuman.nc.network.toClient;

import igentuman.nc.NuclearCraft;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record PacketWorldRadiationData(Map<Long, Long> radiation) implements CustomPacketPayload {
    public PacketWorldRadiationData(long id, Long aLong) {
        this(Map.of(id, aLong));
    }

    public static final CustomPacketPayload.Type<PacketWorldRadiationData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "world_radiation_data_to_client"));
    public static final StreamCodec<ByteBuf, PacketWorldRadiationData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.VAR_LONG, ByteBufCodecs.VAR_LONG), PacketWorldRadiationData::radiation, PacketWorldRadiationData::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

