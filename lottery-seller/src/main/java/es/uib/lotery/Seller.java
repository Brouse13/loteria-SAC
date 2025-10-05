package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.SERVER_HOST;
import static es.uib.lotery.utils.Constants.SERVER_PORT;

@Getter
@Setter
@AllArgsConstructor
public class Seller {
    private static final Logger logger = Logger.getLogger(Seller.class.getName());

    private final String sellerName;
    private final BaseServerConnection connectionServer;
    private final BaseClientConnection connectionClient = new BaseClientConnection();

    public Seller(String sellerName) {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.sellerName = sellerName;
        this.connectionServer = new BaseServerConnection(registry);

        registry.registerHandler(SorteoRequestPacket.class, this::requestSorteo);
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

    private List<BasePacket> requestSorteo(SorteoRequestPacket requestPacket) {
        CompletableFuture<Boolean> hasWin = new CompletableFuture<>();

        if (!connectionClient.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT))) return List.of();

        connectionClient.send(requestPacket, response -> {
            if (!(response instanceof SorteoResponsePacket packet)) return;

            boolean win = packet.isWin();
            hasWin.complete(win);

            logger.config("HasWin - %b with number - %d".formatted(packet.isWin(), requestPacket.getRequestNumber()));
        });

        Boolean result = false;
        try {
            result = hasWin.get(5, TimeUnit.SECONDS);
        }catch (TimeoutException | CancellationException ignore) {
            // If this reaches it means that the server is busy, so we response as failed
        }catch (Exception e) {
            logger.warning(e.getMessage());
        }

        connectionClient.disconnect();

        return List.of(SorteoResponsePacket.builder().win(result).build());
    }
}
