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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Server {
    private final BaseServerConnection connectionServer;
    private final BaseClientConnection connectionClient = new BaseClientConnection();
    private List<Integer> sorteos = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<InetSocketAddress> sellersAddress;

    public Server() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.connectionServer = new BaseServerConnection(registry);

        registry.registerHandler(SorteosRequestPacket.class, this::getLotteryTickets);
    }

    private List<BasePacket> getLotteryTickets(SorteosRequestPacket sorteosRequestPacket) {
        return List.of(new SorteosResponsePacket(sorteos));
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

    public Ticket sortear() {
        Random random = new Random();
        int sorteo = this.sorteos.get(random.nextInt(this.sorteos.size()));

        List<Ticket> sorteoTickets = null;
        for (int i = 0; i < this.tickets.size(); i++) {
            Ticket ticket = this.tickets.get(i);
            if (ticket.getSorteo() == sorteo) {
                sorteoTickets.add(ticket);
                this.tickets.remove(i);
                i--;
            }
        }
        this.sorteos.remove(sorteo);
        return sorteoTickets.get(random.nextInt(sorteoTickets.size()));
    }

    public boolean getSellers(DNSServersRequestPacket request) {
        return connectionClient.send(request, (response) -> {
            if (!(response instanceof DNSServersResponsePacket)) {
                return;
            }

            this.sellersAddress = ((DNSServersResponsePacket) response).getServers();
        });
    }

    public boolean sendResult(DNSServersRequestPacket request) {
        return connectionClient.send(request, (response) -> {

        });
    }

    public void crearSorteo() {
        this.sorteos.add((int) System.nanoTime());
    }

    public boolean requestSorteos(SorteosRequestPacket request) {
        return connectionClient.send(request, (response) -> {
            if (!(response instanceof SorteosResponsePacket)) {
                return;
            }

            this.sorteos = ((SorteosResponsePacket) response).getSorteos();
        });
    }
}
