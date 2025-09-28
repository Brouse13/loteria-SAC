package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public interface ServerConnection {
    boolean start(InetSocketAddress address);

    void stop();

    boolean request(String packetName, Consumer<BasePacket> callback) throws IOException;
}
