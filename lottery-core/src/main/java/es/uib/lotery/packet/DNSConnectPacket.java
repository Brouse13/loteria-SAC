package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSConnectPacket implements BasePacket {
    public enum Type { CONNECT, DISCONNECT }

    public final byte id = PacketBuilder.DNS_REQUEST_PACKET_ID;
    private Type type;
    private String serverName;
    private InetSocketAddress address;


    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putInt(buffer, id);
        PacketUtils.putInt(buffer, type.ordinal());
        PacketUtils.putString(buffer, serverName);
        PacketUtils.putString(buffer, address.getAddress().getHostAddress());
        PacketUtils.putInt(buffer, address.getPort());
    }

    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return DNSConnectPacket.builder()
                .type(Type.values()[PacketUtils.getInt(buffer)])
                .serverName(PacketUtils.getString(buffer))
                .address(new InetSocketAddress(
                        PacketUtils.getString(buffer),
                        PacketUtils.getInt(buffer))
                ).build();
    }
}
