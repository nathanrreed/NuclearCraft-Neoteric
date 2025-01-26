package igentuman.nc.network.toClient;

import igentuman.nc.radiation.client.ClientRadiationData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketWorldRadiationHandler {
    public static void handle(PacketWorldRadiationData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientRadiationData.setWorldRadiation(data.radiation());
        });
    }
}
