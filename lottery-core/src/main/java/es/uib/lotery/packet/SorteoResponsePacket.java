package es.uib.lotery.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Represents a lottery draw response packet sent from the server to the client.
 * <p>
 * Contains information about whether the client has won the lottery draw.
 * Implements {@link BasePacket} for serialization and deserialization.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * SorteoResponsePacket response = SorteoResponsePacket.builder()
 *     .win(true)
 *     .build();
 *
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * response.encode(buffer);
 * buffer.flip();
 * SorteoResponsePacket decoded = (SorteoResponsePacket) response.decode(buffer);
 * }</pre>
 *
 * @see BasePacket
 * @see PacketBuilder
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SorteoResponsePacket implements BasePacket {

    /** Unique packet ID for SorteoResponsePacket. */
    public final byte id = PacketBuilder.SORTEO_RESPONSE_PACKET_ID;

    /** Indicates whether the client won the lottery. */
    private boolean win;

    /**
     * Encodes this packet into the provided {@link ByteBuffer}.
     * <p>
     * Serialization format:
     * <ul>
     *     <li>1 byte: packet ID</li>
     *     <li>1 byte: win flag (true/false)</li>
     * </ul>
     * </p>
     *
     * @param buffer the {@link ByteBuffer} to write the packet data into
     */
    @Override
    public void encode(final ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putBoolean(buffer, win);
    }

    /**
     * Decodes a {@link ByteBuffer} into a {@link SorteoResponsePacket}.
     *
     * @param buffer the buffer containing the serialized packet data
     * @return a new {@link SorteoResponsePacket} instance
     */
    @Override
    public BasePacket decode(final ByteBuffer buffer) {
        return SorteoResponsePacket.builder()
                .win(PacketUtils.getBoolean(buffer))
                .build();
    }
}
