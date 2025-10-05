package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Represents a DNS response packet sent from the DNS server to a client.
 * <p>
 * Contains the {@link InetSocketAddress} of a lottery server that the client
 * can connect to. Implements {@link BasePacket} for serialization and deserialization.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * DNSResponsePacket response = DNSResponsePacket.builder()
 *     .address(new InetSocketAddress("127.0.0.1", 12345))
 *     .build();
 *
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * response.encode(buffer);
 * buffer.flip();
 * DNSResponsePacket decoded = (DNSResponsePacket) response.decode(buffer);
 * }</pre>
 *
 * @see BasePacket
 * @see DNSRequestPacket
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSResponsePacket implements BasePacket {

    /** Unique packet ID for DNSResponsePacket. */
    public final byte id = PacketBuilder.DNS_RESPONSE_PACKET_ID;

    /** The address of the lottery server provided by the DNS. */
    private InetSocketAddress address;

    /**
     * Encodes this packet into the provided {@link ByteBuffer}.
     * <p>
     * Serialization format:
     * <ul>
     *     <li>1 byte: packet ID</li>
     *     <li>variable: server host name string</li>
     *     <li>4 bytes: server port</li>
     * </ul>
     * </p>
     *
     * @param buffer the buffer to write the packet data into
     */
    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putString(buffer, address.getHostName());
        PacketUtils.putInt(buffer, address.getPort());
    }

    /**
     * Decodes a {@link ByteBuffer} into a new {@link DNSResponsePacket}.
     *
     * @param buffer the buffer containing the serialized packet data
     * @return a new {@link DNSResponsePacket} instance
     */
    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return DNSResponsePacket.builder()
                .address(new InetSocketAddress(
                        PacketUtils.getString(buffer),
                        PacketUtils.getInt(buffer))
                ).build();
    }
}
