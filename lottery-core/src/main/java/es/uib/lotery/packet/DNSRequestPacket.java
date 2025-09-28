package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSRequestPacket implements BasePacket {
    @Getter public static final byte id = PacketBuilder.DNS_REQUEST_PACKET_ID;
    @Getter private String serverName;

    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putString(buffer, serverName);
    }

    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return DNSRequestPacket.builder()
                .serverName(PacketUtils.getString(buffer))
                .build();
    }
}
