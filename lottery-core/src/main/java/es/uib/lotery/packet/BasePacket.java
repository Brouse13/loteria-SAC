package es.uib.lotery.packet;

import java.nio.ByteBuffer;

/**
 * Represents a generic packet for the lottery system.
 * <p>
 * All packet types must implement this interface to provide
 * serialization and deserialization logic using {@link ByteBuffer}.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * BasePacket packet = new SorteoRequestPacket(...);
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * packet.encode(buffer);
 * buffer.flip();
 * BasePacket decoded = packet.decode(buffer);
 * }</pre>
 *
 * @see SorteoRequestPacket
 * @see SorteoResponsePacket
 * @see DNSConnectPacket
 * @see DNSRequestPacket
 * @see DNSResponsePacket
 */
public interface BasePacket {

    /**
     * Returns the unique packet ID.
     *
     * @return the packet ID as a byte
     */
    byte getId();

    /**
     * Serializes this packet into the provided {@link ByteBuffer}.
     *
     * @param buffer the buffer to write the packet data into
     */
    void encode(ByteBuffer buffer);

    /**
     * Deserializes a {@link ByteBuffer} into a new packet instance.
     *
     * @param buffer the buffer containing the serialized packet data
     * @return a new {@link BasePacket} instance representing the decoded packet
     */
    BasePacket decode(ByteBuffer buffer);
}
