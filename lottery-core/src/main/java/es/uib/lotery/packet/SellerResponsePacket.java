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
public class SellerResponsePacket implements BasePacket {
    public final byte id = PacketBuilder.SELLER_REQUEST_PACKET_ID;
    private boolean win;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putBoolean(buffer, win);
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SellerResponsePacket.builder()
                .win(PacketUtils.getBoolean(buffer))
                .build();
    }
}
