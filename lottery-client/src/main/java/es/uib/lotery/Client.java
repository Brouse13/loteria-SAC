package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.*;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cliente de lotería orientado a objetos.
 * Usa BaseClientConnection para gestionar los sockets.
 */
@Getter
@Setter
public class Client {
    private final String name;
    private final List<Ticket> tickets = new ArrayList<>();
    private final BaseClientConnection connection = new BaseClientConnection();
    private InetSocketAddress DNSAddress;

    public Client(String name) {
        this.name = name;
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public boolean connectToServer(String host, int port) {
        return connection.connect(new InetSocketAddress(host, port));
    }

    public boolean connectToServer() {
        return connection.connect(new InetSocketAddress(this.DNSAddress.getAddress(), this.DNSAddress.getPort()));
    }

    public void disconnect() {
        connection.disconnect();
    }

    public boolean DNSConnectionTicket(DNSRequestPacket dnsRequest) {
        return connection.send(dnsRequest, (response) -> {
            if(! (response instanceof DNSResponsePacket)) {return;}

            this.DNSAddress = ((DNSResponsePacket) response).getAddress();
        });
    }

    public boolean recieveTicket(SellerRequestPacket requestPacket) {
        return connection.send(requestPacket, (response) -> {
            if(!(response instanceof SellerResponsePacket sellerResponsePacket)) return;

            tickets.add(sellerResponsePacket.getTicket());
        });
    }

    public void listTickets() {
        System.out.println("[CLIENT-OBJ] Boletos de " + name + ": " + tickets);
    }
}

