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
public class DrawRequestPacket implements BasePacket {
    public final byte id = PacketBuilder.DRAW_REQUEST_PACKET_ID;
    private int drawId;
    private String drawNumber;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putInt(buffer, drawId);
        PacketUtils.putString(buffer, drawNumber);
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return DrawRequestPacket.builder()
                .drawId(PacketUtils.getInt(buffer))
                .drawNumber(PacketUtils.getString(buffer))
                .build();
    }
}
