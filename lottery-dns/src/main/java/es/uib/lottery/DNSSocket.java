package es.uib.lottery;

import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.connection.ServerConnection;
import es.uib.lotery.packet.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static es.uib.lotery.utils.Constants.DNS_HOST;
import static es.uib.lotery.utils.Constants.DNS_PORT;

public class DNSSocket {
    private final ServerConnection serverConnection;
    private final Map<String, Queue<InetSocketAddress>> serversNames = new ConcurrentHashMap<>();

    public DNSSocket() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.serverConnection = new BaseServerConnection(registry);

        registry.registerHandler(DNSRequestPacket.class, this::handleDNS);
        registry.registerHandler(DNSConnectPacket.class, this::handleConnection);
        registry.registerHandler(DNSServersRequestPacket.class, this::handleServers);
    }

    public void start() throws IOException {
        serverConnection.start(new InetSocketAddress(DNS_HOST, DNS_PORT));
        serverConnection.listen();
    }

    public void stop() {
        serverConnection.stop();
    }

    private List<BasePacket> handleDNS(DNSRequestPacket packet) {
        DNSResponsePacket.DNSResponsePacketBuilder builder = DNSResponsePacket.builder();

        Queue<InetSocketAddress> addresses = serversNames.get(packet.getServerName());
        if (addresses == null) return List.of(builder.build());

        // Apply roundRobin policy
        InetSocketAddress address = addresses.poll();
        addresses.add(address);

        return List.of(builder.address(address).build());

    }

    private List<BasePacket> handleConnection(DNSConnectPacket packet) {
        serversNames.computeIfPresent(packet.getServerName(), (name, addresses) -> {
            switch (packet.getType()) {
                case CONNECT -> addresses.add(packet.getAddress());
                case DISCONNECT -> addresses.remove(packet.getAddress());
            }
            return addresses;
        });

        return List.of();
    }

    private List<BasePacket> handleServers(DNSServersRequestPacket packet) {
        // Suponiendo que quieres todos los servidores de todas las colas
        List<InetSocketAddress> allServers = new ArrayList<>();

        for (Queue<InetSocketAddress> queue : serversNames.values()) {
            allServers.addAll(queue);
        }

        // Construimos un único paquete con todos los servidores
        DNSServersResponsePacket response = DNSServersResponsePacket.builder()
                .servers(allServers)
                .build();

        return List.of(response);
    }




}
