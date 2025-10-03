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
public class SorteosRequestPacket implements BasePacket {
    public final byte id = PacketBuilder.SELLER_REQUEST_PACKET_ID;
    private int sorteoId;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putInt(buffer, sorteoId);
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SorteosRequestPacket.builder()
                .sorteoId(PacketUtils.getInt(buffer))
                .build();
    }
}