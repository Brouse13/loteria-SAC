package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSResponsePacket implements BasePacket {
    @Getter public static final byte id = PacketBuilder.DNS_RESPONSE_PACKET_ID;
    @Getter private InetSocketAddress address;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putString(buffer, address.getAddress().getHostAddress());
        PacketUtils.putInt(buffer, address.getPort());
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return DNSResponsePacket.builder()
                .address(new InetSocketAddress(
                        PacketUtils.getString(buffer),
                        PacketUtils.getInt(buffer))
                ).build();
    }
}
