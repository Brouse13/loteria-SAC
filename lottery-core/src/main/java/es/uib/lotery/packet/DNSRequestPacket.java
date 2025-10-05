package es.uib.lotery.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Represents a DNS request packet sent from a client to the DNS server.
 * <p>
 * This packet is used to request the address of a lottery server.
 * Implements {@link BasePacket} for serialization and deserialization.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * DNSRequestPacket request = new DNSRequestPacket();
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * request.encode(buffer);
 * buffer.flip();
 * DNSRequestPacket decoded = (DNSRequestPacket) request.decode(buffer);
 * }</pre>
 *
 * @see BasePacket
 * @see DNSResponsePacket
 */
@Getter
@NoArgsConstructor
public class DNSRequestPacket implements BasePacket {

    /** Unique packet ID for DNSRequestPacket. */
    public final byte id = PacketBuilder.DNS_REQUEST_PACKET_ID;

    /**
     * Encodes this packet into the provided {@link ByteBuffer}.
     * <p>
     * Serialization format:
     * <ul>
     *     <li>1 byte: packet ID</li>
     * </ul>
     * </p>
     *
     * @param buffer the buffer to write the packet data into
     */
    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
    }

    /**
     * Decodes a {@link ByteBuffer} into a new {@link DNSRequestPacket}.
     *
     * @param buffer the buffer containing the serialized packet data
     * @return a new {@link DNSRequestPacket} instance
     */
    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return new DNSRequestPacket();
    }
}
