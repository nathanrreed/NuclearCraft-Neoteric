package igentuman.nc.network.toClient;

import igentuman.nc.radiation.client.ClientRadiationData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketPlayerRadiationHandler {
    public static void handle(PacketPlayerRadiationData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientRadiationData.setPlayerRadiation(data.playerRadiation());
        });
    }
}