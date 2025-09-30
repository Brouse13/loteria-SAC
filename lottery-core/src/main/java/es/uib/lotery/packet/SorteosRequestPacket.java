package es.uib.lotery.packet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@NoArgsConstructor
@Builder
public class SorteosRequestPacket implements BasePacket {
    public final byte id = PacketBuilder.SELLER_REQUEST_PACKET_ID;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SellerRequestPacket.builder().build();
    }
}