package es.uib.lotery.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Builder and factory for creating {@link BasePacket} instances from {@link ByteBuffer} data.
 * <p>
 * Provides packet ID constants and a centralized method to construct packets dynamically
 * based on their ID. Typically used when receiving raw packet data from a client or server.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ByteBuffer buffer = ... // received from network
 * BasePacket packet = PacketBuilder.INSTANCE.buildPacket(buffer);
 * }</pre>
 *
 * @see BasePacket
 * @see PacketUtils
 */
public class PacketBuilder {

    /** Packet ID for {@link DNSRequestPacket}. */
    public static final byte DNS_REQUEST_PACKET_ID = 0x00;

    /** Packet ID for {@link DNSResponsePacket}. */
    public static final byte DNS_RESPONSE_PACKET_ID = 0x01;

    /** Packet ID for {@link SorteoRequestPacket}. */
    public static final byte SORTEO_REQUEST_PACKET_ID = 0x02;

    /** Packet ID for {@link SorteoResponsePacket}. */
    public static final byte SORTEO_RESPONSE_PACKET_ID = 0x03;

    /** Packet ID for {@link DNSConnectPacket}. */
    public static final byte DNS_CONNECT_PACKET_ID = 0x04;

    /** Singleton instance of {@link PacketBuilder}. */
    public static final PacketBuilder INSTANCE = new PacketBuilder();

    /** Private constructor to enforce singleton usage. */
    private PacketBuilder() {}

    /**
     * Constructs a {@link BasePacket} from the provided {@link ByteBuffer}.
     * <p>
     * The first byte in the buffer is used to determine the packet type.
     * The remaining buffer is passed to the corresponding packet's {@link BasePacket#decode(ByteBuffer)}
     * method to fully populate the packet fields.
     * </p>
     *
     * @param byteBuffer the buffer containing the serialized packet data
     * @return a {@link BasePacket} instance corresponding to the packet ID, or {@code null} if no match
     * @throws IOException if an I/O error occurs during decoding
     */
    public BasePacket buildPacket(ByteBuffer byteBuffer) throws IOException {
        BasePacket packet = null;
        int id = PacketUtils.getByte(byteBuffer);

        // Determine packet type based on ID
        switch (id) {
            case DNS_REQUEST_PACKET_ID -> packet = new DNSRequestPacket();
            case DNS_RESPONSE_PACKET_ID -> packet = new DNSResponsePacket();
            case DNS_CONNECT_PACKET_ID -> packet = new DNSConnectPacket();
            case SORTEO_REQUEST_PACKET_ID -> packet = new SorteoRequestPacket();
            case SORTEO_RESPONSE_PACKET_ID -> packet = new SorteoResponsePacket();
        }

        // Decode the packet if a valid type was found
        if (packet != null) packet = packet.decode(byteBuffer);
        return packet;
    }
}
