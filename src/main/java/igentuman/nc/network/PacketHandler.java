package igentuman.nc.network;

import igentuman.nc.network.toClient.PacketPlayerRadiationData;
import igentuman.nc.network.toClient.PacketPlayerRadiationHandler;
import igentuman.nc.network.toClient.PacketWorldRadiationData;
import igentuman.nc.network.toClient.PacketWorldRadiationHandler;
import igentuman.nc.network.toServer.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

    @SubscribeEvent
    public void clientToServerUpdate(RegisterPayloadHandlersEvent event) {
        //Client to server messages
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(PacketSliderChanged.TYPE, PacketSliderChanged.STREAM_CODEC, PacketSliderChangedHandler::handle);
        registrar.playToServer(PacketGuiButtonPress.TYPE, PacketGuiButtonPress.STREAM_CODEC, PacketGuiButtonPressHandler::handle);
        registrar.playToServer(PacketSideConfigToggle.TYPE, PacketSideConfigToggle.STREAM_CODEC, PacketSideConfigToggleHandler::handle);
        registrar.playToServer(PacketFlushSlotContent.TYPE, PacketFlushSlotContent.STREAM_CODEC, PacketFlushSlotContentHandler::handle);

        //Server to client messages
        registrar.playToClient(PacketPlayerRadiationData.TYPE, PacketPlayerRadiationData.STREAM_CODEC, PacketPlayerRadiationHandler::handle);
        registrar.playToClient(PacketWorldRadiationData.TYPE, PacketWorldRadiationData.STREAM_CODEC, PacketWorldRadiationHandler::handle);
    }
}