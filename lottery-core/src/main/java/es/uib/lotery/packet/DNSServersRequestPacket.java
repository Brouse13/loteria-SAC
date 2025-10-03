package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSServersRequestPacket implements BasePacket {
    public final byte id = PacketBuilder.DNS_REQUEST_PACKET_ID;
    private String serverName;

    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
    }

    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return DNSServersRequestPacket.builder()
                .serverName(PacketUtils.getString(buffer))
                .build();
    }
}