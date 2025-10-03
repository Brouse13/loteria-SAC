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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@AllArgsConstructor
public class Server {
    private final BaseServerConnection connectionServer;
    private final BaseClientConnection connectionClient = new BaseClientConnection();
    private Map<Integer, List<Ticket>> sorteos = new ConcurrentHashMap<>();
    private Set<Integer> raffledSorteos = Collections.synchronizedSet(new HashSet<>());
    private List<InetSocketAddress> sellersAddress;

    public Server() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.connectionServer = new BaseServerConnection(registry);

        registry.registerHandler(SorteosRequestPacket.class, this::getLotteryTickets);
        registry.registerHandler(SellerRequestPacket.class, this::createTicket);
    }

    private List<BasePacket> createTicket(SellerRequestPacket request) {
        return List.of();
    }

    private List<BasePacket> getLotteryTickets(SorteosRequestPacket request) {
        return List.of(new SorteosResponsePacket(sorteos.keySet().stream().toList()));
    }

    public void startServer(String host, int port) throws IOException {
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

    public Ticket sortear(int sorteoId) {
        Random random = new Random();

        List<Ticket> tickets = sorteos.get(sorteoId);
        return tickets.get(random.nextInt(tickets.size()));
    }

    public void getSellers(DNSServersRequestPacket request) {
        connectionClient.send(request, (response) -> {
            if (!(response instanceof DNSServersResponsePacket)) return;

            this.sellersAddress = ((DNSServersResponsePacket) response).getServers();
        });
    }

    public boolean sendResult(DNSServersRequestPacket request) {
        return connectionClient.send(request, (response) -> {

        });
    }

    public void crearSorteo() {
        this.sorteos.put((int) System.nanoTime(), new ArrayList<>());
    }
}
