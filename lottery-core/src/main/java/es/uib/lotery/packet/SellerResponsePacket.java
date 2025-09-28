package es.uib.lotery.packet;

import es.uib.lotery.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerResponsePacket implements BasePacket {
    @Getter
    public static final byte id = PacketBuilder.SELLER_REQUEST_PACKET_ID;
    @Getter
    private Ticket ticket;

    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putString(buffer, ticket.getNumber());
        PacketUtils.putInt(buffer, ticket.getSorteo());
    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SellerResponsePacket.builder()
                .ticket(Ticket.builder()
                        .number(PacketUtils.getString(buffer))
                        .sorteo(PacketUtils.getInt(buffer))
                        .build()
                ).build();
    }
}
