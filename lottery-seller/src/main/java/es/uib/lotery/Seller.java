package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Seller {
    private final String sellerId;
    private final BaseServerConnection connectionServer;
    private final BaseClientConnection connectionClient = new BaseClientConnection();
    private final PacketHandleRegistry registry;
    private final InetSocketAddress ServerAddress = new InetSocketAddress("localhost", 8090);
    private InetSocketAddress DNSAddress;
    private List<Integer> sorteos;

    public Seller(String sellerId) {
        this.sellerId = sellerId;
        this.registry = new PacketHandleRegistry();
        registerHandlers();
        this.connectionServer = new BaseServerConnection(registry);
    }

    private void registerHandlers() {
        // Handler para compras
        registry.registerHandler(SellerRequestPacket.class, (buy) -> {
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
        });
    }

    public boolean connectServer(String host, int port) {
           return this.connectionServer.start(new InetSocketAddress(host, port));
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

    public boolean requestSorteos(SorteosRequestPacket request){
        return connectionClient.send(request, (response) -> {
            if(! (response instanceof SorteosResponsePacket)) {return;}

            this.sorteos = ((SorteosResponsePacket) response).getSorteos();
        });
    }
}
