package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    private final PacketHandleRegistry registry;
    private final InetSocketAddress ServerAddress = new InetSocketAddress("localhost", 8090);
    private InetSocketAddress DNSAddress;
    private List<Integer> sorteos;
    private List<InetSocketAddress> sellersAddress;
    private List<Ticket> tickets;

    public Server() {
        this.registry = new PacketHandleRegistry();
        registerHandlers();
        this.connectionServer = new BaseServerConnection(registry);
    }

    private void registerHandlers() {
        // Handler para compras
        registry.registerHandler(SorteosRequestPacket.class, (buy) -> {
            // Construimos el paquete de respuesta
            SorteosResponsePacket response = SorteosResponsePacket.builder()
                    .sorteos(this.sorteos)
                    .build();

            return List.of(response);
        });
    }

    public boolean connectServer() {
        return this.connectionServer.start(new InetSocketAddress(this.ServerAddress.getAddress(), this.ServerAddress.getPort()));
    }

    public void disconnectServer() {
        this.connectionServer.stop();
    }

    public void listen(){
        try{
            this.connectionServer.listen();
        } catch (Exception e) {
            System.out.println("LISTENING ERROR"+e.getMessage());
        }
    }

    public boolean connectClient() {
        return this.connectionClient.connect(new InetSocketAddress(this.ServerAddress.getAddress(), this.ServerAddress.getPort()));
    }

    public void disconnectClient() {
        connectionClient.disconnect();
    }

    public Ticket sortear(){
        Random random = new Random();
        int sorteo = this.sorteos.get(random.nextInt(this.sorteos.size()));

        List<Ticket> sorteoTickets = null;
        for (int i = 0; i < this.tickets.size(); i++) {
            Ticket ticket = this.tickets.get(i);
            if (ticket.getSorteo() == sorteo){
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
            if(!(response instanceof DNSServersResponsePacket)){return;}

            this.sellersAddress = ((DNSServersResponsePacket) response).getServers();
        });
    }

    public boolean sendResult(DNSServersRequestPacket request) {
        return connectionClient.send(request, (response) -> {

        });
    }

    public void crearSorteo(){
        this.sorteos.add((int) System.nanoTime());
    }

    public boolean requestSorteos(SorteosRequestPacket request){
        return connectionClient.send(request, (response) -> {
            if(! (response instanceof SorteosResponsePacket)) {return;}

            this.sorteos = ((SorteosResponsePacket) response).getSorteos();
        });
    }
}
