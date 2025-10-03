package es.uib.lotery;

import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

import static es.uib.lotery.utils.Constants.RANDOM_NUMBER;

@Getter
@Setter
@AllArgsConstructor
public class Server {
    private Random rand = new Random();
    private final BaseServerConnection connectionServer;
    private int sorteo;

    public Server() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.connectionServer = new BaseServerConnection(registry);

        sorteo = RANDOM_NUMBER.get();

        System.out.println("Server started - " + sorteo);

        registry.registerHandler(SorteoRequestPacket.class, this::sortearNumero);
    }

    private List<BasePacket> sortearNumero(SorteoRequestPacket request) {
        SorteoResponsePacket build = SorteoResponsePacket.builder()
                .win(request.getRequestNumber() == sorteo)
                .build();

        System.out.println(request.getRequestNumber() + " -------------------- " + sorteo);

        if (build.isWin()) sorteo = RANDOM_NUMBER.get();

        return List.of(build);
    }

    public void startServer(String host, int port) throws IOException {
        connectionServer.start(new InetSocketAddress(host, port));
        connectionServer.listen();
    }

    public void disconnectServer() {
        this.connectionServer.stop();
    }
}
