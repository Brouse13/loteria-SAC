package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Represents a lottery draw request packet sent from a client to the server.
 * <p>
 * Contains the number requested by the client for the lottery draw.
 * Implements {@link BasePacket} for serialization and deserialization.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * SorteoRequestPacket request = SorteoRequestPacket.builder()
 *     .requestNumber(42)
 *     .build();
 *
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * request.encode(buffer);
 * buffer.flip();
 * SorteoRequestPacket decoded = (SorteoRequestPacket) request.decode(buffer);
 * }</pre>
 *
 * @see BasePacket
 * @see PacketBuilder
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SorteoRequestPacket implements BasePacket {

    /** Unique packet ID for SorteoRequestPacket. */
    public final byte id = PacketBuilder.SORTEO_REQUEST_PACKET_ID;

    /** The number requested by the client for the lottery draw. */
    private int requestNumber;

    /**
     * Encodes this packet into the provided {@link ByteBuffer}.
     * <p>
     * Serialization format:
     * <ul>
     *     <li>1 byte: packet ID</li>
     *     <li>4 bytes: requested lottery number</li>
     * </ul>
     * </p>
     *
     * @param buffer the {@link ByteBuffer} to write the packet data into
     */
    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putInt(buffer, requestNumber);
    }

    /**
     * Decodes a {@link ByteBuffer} into a {@link SorteoRequestPacket}.
     *
     * @param buffer the buffer containing the serialized packet data
     * @return a new {@link SorteoRequestPacket} instance
     */
    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SorteoRequestPacket.builder()
                .requestNumber(PacketUtils.getInt(buffer))
                .build();
    }

    /**
     * Returns a string representation of the packet.
     *
     * @return a string including the packet ID and requested number
     */
    @Override
    public String toString() {
        return "SorteoRequestPacket{" +
                "id=" + id +
                ", requestNumber=" + requestNumber +
                '}';
    }
}
