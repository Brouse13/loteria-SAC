package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
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
    private final List<Ticket> tickets;
    private final BaseClientConnection connection;
    private InetSocketAddress DNSAddress;

    public Client(String name) {
        this.name = name;
        this.tickets = new ArrayList<Ticket>();
        this.connection = new BaseClientConnection();
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

    public boolean DNSConnectionTicket(BasePacket dnsRequest) {

        DNSRequestPacket dnsPacket = (DNSRequestPacket) dnsRequest;
        return connection.send(dnsPacket, (response) -> {
            if(! (response instanceof DNSResponsePacket)) {return;};

            this.DNSAddress = ((DNSResponsePacket) response).getAddress();

        });
    }

    public boolean recieveTicket(BasePacket recievePacket) {
        SellerRequestPacket lasPaquet = (SellerRequestPacket) recievePacket;
        return connection.send(lasPaquet, (response) -> {
            if(!( response instanceof SellerResponsePacket)) {return;};

            //tengo que hacer que el paquete devuelva cosas :P

            Ticket ticket = new Ticket();
            tickets.add(ticket);
        });
    }

    public void listTickets() {
        System.out.println("[CLIENT-OBJ] Boletos de " + name + ": " + tickets);
    }
}

@Setter
@Getter
@AllArgsConstructor
class Ticket{
    private String number;
    private int sorteo;
}
