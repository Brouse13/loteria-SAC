package es.uib.lotery.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@NoArgsConstructor
public class DNSRequestPacket implements BasePacket {
    public final byte id = PacketBuilder.DNS_REQUEST_PACKET_ID;

    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
    }

    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return new DNSRequestPacket();
    }
}
