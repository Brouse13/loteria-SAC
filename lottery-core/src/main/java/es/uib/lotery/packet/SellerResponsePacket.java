package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerResponsePacket implements BasePacket {
    @Getter public static final byte id = PacketBuilder.SELLER_REQUEST_PACKET_ID;
    @Getter private InetSocketAddress address;


    @Override
    public void encode(final ByteBuffer buffer) {

    }

    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return null;
    }
}
