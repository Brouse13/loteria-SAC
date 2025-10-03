package es.uib.lotery;

import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class Server {
    private final BaseServerConnection connectionServer;
    private int sorteo;

    public Server() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.connectionServer = new BaseServerConnection(registry);

        registry.registerHandler(SorteoRequestPacket.class, this::sortearNumero);
    }

    private List<BasePacket> sortearNumero(SorteoRequestPacket request) {
        return List.of(SorteoResponsePacket.builder()
                .win(request.getRequestNumber() == sorteo)
                .build()
        );
    }

    public void startServer(String host, int port) throws IOException {
        connectionServer.start(new InetSocketAddress(host, port));
        connectionServer.listen();
    }

    public void disconnectServer() {
        this.connectionServer.stop();
    }
}
