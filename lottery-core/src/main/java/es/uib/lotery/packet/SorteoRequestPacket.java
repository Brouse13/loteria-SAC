package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SorteoRequestPacket implements BasePacket {
    public final byte id = PacketBuilder.SELLER_REQUEST_PACKET_ID;
    private int requestNumber;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putInt(buffer, requestNumber);
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SorteoRequestPacket.builder()
                .requestNumber(PacketUtils.getInt(buffer))
                .build();
    }

    @Override
    public String toString() {
        return "SorteoRequestPacket{" +
                "id=" + id +
                ", requestNumber=" + requestNumber +
                '}';
    }
}
