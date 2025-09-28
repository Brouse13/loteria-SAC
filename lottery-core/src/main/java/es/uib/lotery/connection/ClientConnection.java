package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public interface ClientConnection {
    boolean connect(InetSocketAddress address);

    void disconnect();

    boolean send(BasePacket packet, Consumer<BasePacket> responseCallback);
}
