package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Seller {
    private final String sellerName;
    private final BaseServerConnection connectionServer;
    private final BaseClientConnection connectionClient = new BaseClientConnection();
    private List<Integer> sorteos;

    public Seller(String sellerName) {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.sellerName = sellerName;
        this.connectionServer = new BaseServerConnection(registry);

        registry.registerHandler(SellerRequestPacket.class, this::requestTicket);
    }

    public void start(String host, int port) throws IOException {
        connectionServer.start(new InetSocketAddress(host, port));
        connectionServer.listen();
    }

    public void disconnectServer() {
        this.connectionServer.stop();
    }

    public boolean connectClient(String host, int port) {
        return this.connectionClient.connect(new InetSocketAddress(host, port));
    }

    public void disconnectClient() {
        connectionClient.disconnect();
    }

    public void connectDNS(String host, int port) {
        DNSConnectPacket request = DNSConnectPacket.builder()
                .address(new InetSocketAddress(host, port))
                .serverName(sellerName)
                .type(DNSConnectPacket.Type.CONNECT)
                .build();

        connectionClient.send(request, null);
    }

    public void disconnectDNS(String host, int port) {
        DNSConnectPacket request = DNSConnectPacket.builder()
                .serverName(sellerName)
                .type(DNSConnectPacket.Type.DISCONNECT)
                .address(new InetSocketAddress(host, port))
                .build();

        connectionClient.send(request, null);
    }

    public void requestSorteos() {
        connectionClient.send(new SorteosRequestPacket(), (packet) -> {
            if (!(packet instanceof SorteosResponsePacket response)) return;

            this.sorteos = response.getSorteos();
        });
    }

    private List<BasePacket> requestTicket(SellerRequestPacket requestPacket) {
        Random r = new Random();
        Ticket ticket = Ticket.builder()
                .number("T-" + System.nanoTime())
                .sorteo(this.sorteos.get(r.nextInt(this.sorteos.size())))
                .build();

        // Construimos el paquete de respuesta
        SellerResponsePacket response = SellerResponsePacket.builder()
                .ticket(ticket)
                .build();

        return List.of(response);
    }
}
