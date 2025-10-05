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
    private final Random random = new Random();
    private final ServerConnection serverConnection;
    private final Map<String, InetSocketAddress> serversNames = new ConcurrentHashMap<>();

    public DNSSocket() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.serverConnection = new BaseServerConnection(registry);

        registry.registerHandler(DNSRequestPacket.class, this::handleDNS);
        registry.registerHandler(DNSConnectPacket.class, this::handleConnection);
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

        InetSocketAddress address = getRandomServer();
        if (address == null) return List.of(builder.build());

        return List.of(builder.address(address).build());
    }

    private List<BasePacket> handleConnection(DNSConnectPacket packet) {
        switch (packet.getType()) {
            case CONNECT ->  serversNames.put(packet.getServerName(), packet.getAddress());
            case DISCONNECT -> serversNames.remove(packet.getServerName());
        }

        return List.of();
    }

    private InetSocketAddress getRandomServer() {
        return serversNames.values().stream()
                .skip(random.nextInt(serversNames.size()))
                .findFirst()
                .orElse(null);
    }
}
