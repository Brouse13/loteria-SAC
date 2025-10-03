package es.uib.lotery.packet;

import es.uib.lotery.entity.Ticket;
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

    @Override
    public String toString() {
        return "SellerResponsePacket{" +
                "id=" + id +
                ", ticket=" + ticket +
                '}';
    }
}
