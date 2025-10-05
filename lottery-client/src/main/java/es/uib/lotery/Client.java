package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.packet.*;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * Cliente de lotería orientado a objetos.
 * Usa BaseClientConnection para gestionar los sockets.
 */
@Getter
@Setter
public class Client {
    private final BaseClientConnection connection = new BaseClientConnection();
    private boolean win = false;

    public boolean connectToServer(String host, int port) {
        return connection.connect(new InetSocketAddress(host, port));
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void requestAddress(DNSRequestPacket dnsRequest, Consumer<InetSocketAddress> address) {
        connection.send(dnsRequest, (response) -> {
            if (!(response instanceof DNSResponsePacket)) return;

            address.accept(((DNSResponsePacket) response).getAddress());
        });
    }

    public void pedirSorteo(SorteoRequestPacket requestPacket) {
        win = false;

        connection.send(requestPacket, (packet) -> {
            if (!(packet instanceof SorteoResponsePacket)) return;

            if (((SorteoResponsePacket) packet).isWin()) win = true;
        });
    }

    public boolean hasWin() {
        return win;
    }
}

