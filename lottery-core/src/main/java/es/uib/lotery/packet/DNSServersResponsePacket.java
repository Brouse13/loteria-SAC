package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSServersResponsePacket implements BasePacket {
    public final byte id = PacketBuilder.DNS_RESPONSE_PACKET_ID;
    private List<InetSocketAddress> servers;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);

        // Escribir el tamaño de la lista
        PacketUtils.putInt(buffer, servers.size());

        // Escribir cada servidor: host + puerto
        for (InetSocketAddress server : servers) {
            PacketUtils.putString(buffer, server.getHostString());
            PacketUtils.putInt(buffer, server.getPort());
        }
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        // Leer cantidad de servidores
        int size = PacketUtils.getInt(buffer);

        // Lista temporal de servidores
        List<InetSocketAddress> serversList = new ArrayList<>(size);

        // Leer cada servidor: host + puerto
        for (int i = 0; i < size; i++) {
            String host = PacketUtils.getString(buffer);
            int port = PacketUtils.getInt(buffer);
            serversList.add(new InetSocketAddress(host, port));
        }

        // Devolver usando el builder
        return DNSServersResponsePacket.builder()
                .servers(serversList)
                .build();
    }
}
