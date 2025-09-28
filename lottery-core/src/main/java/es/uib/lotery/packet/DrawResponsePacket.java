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
public class DrawResponsePacket implements BasePacket {
    public final byte id = PacketBuilder.DRAW_RESPONSE_PACKET_ID;
    private boolean hasPrice;
    private int price;

    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putBoolean(buffer, hasPrice);
        PacketUtils.putInt(buffer, price);
    }

    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return DrawResponsePacket.builder()
                .hasPrice(PacketUtils.getBoolean(buffer))
                .price(PacketUtils.getInt(buffer))
                .build();
    }
}
