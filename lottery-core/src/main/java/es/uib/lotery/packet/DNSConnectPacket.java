package es.uib.lotery.packet;

import lombok.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Represents a DNS connection packet used to notify the DNS server
 * about a server's connection or disconnection.
 * <p>
 * Implements {@link BasePacket} for serialization and deserialization.
 * The packet can indicate either a connection or a disconnection using the {@link Type} enum.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * DNSConnectPacket connectPacket = DNSConnectPacket.builder()
 *     .type(DNSConnectPacket.Type.CONNECT)
 *     .serverName("Seller1")
 *     .address(new InetSocketAddress("127.0.0.1", 12345))
 *     .build();
 *
 * ByteBuffer buffer = ByteBuffer.allocate(256);
 * connectPacket.encode(buffer);
 * buffer.flip();
 * DNSConnectPacket decoded = (DNSConnectPacket) connectPacket.decode(buffer);
 * }</pre>
 *
 * @see BasePacket
 * @see PacketBuilder
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DNSConnectPacket implements BasePacket {

    /** Defines the type of DNS packet: connection or disconnection. */
    public enum Type { CONNECT, DISCONNECT }

    /** Unique packet ID for DNSConnectPacket. */
    public final byte id = PacketBuilder.DNS_CONNECT_PACKET_ID;

    /** Type of the DNS packet, either {@link Type#CONNECT} or {@link Type#DISCONNECT}. */
    private Type type;

    /** Name of the server connecting or disconnecting. */
    private String serverName;

    /** Address of the server connecting or disconnecting. */
    private InetSocketAddress address;

    /**
     * Encodes this packet into the provided {@link ByteBuffer}.
     * <p>
     * Serialization format:
     * <ul>
     *     <li>1 byte: packet ID</li>
     *     <li>4 bytes: type ordinal</li>
     *     <li>variable: server name string</li>
     *     <li>variable: server IP address string</li>
     *     <li>4 bytes: server port</li>
     * </ul>
     * </p>
     *
     * @param buffer the {@link ByteBuffer} to write the packet data into
     */
    @Override
    public void encode(ByteBuffer buffer) {
        PacketUtils.putByte(buffer, id);
        PacketUtils.putInt(buffer, type.ordinal());
        PacketUtils.putString(buffer, serverName);
        PacketUtils.putString(buffer, address.getAddress().getHostAddress());
        PacketUtils.putInt(buffer, address.getPort());
    }

    /**
     * Decodes a {@link ByteBuffer} into a {@link DNSConnectPacket}.
     *
     * @param buffer the buffer containing the serialized packet data
     * @return a new {@link DNSConnectPacket} instance
     */
    @Override
    public BasePacket decode(ByteBuffer buffer) {
        return DNSConnectPacket.builder()
                .type(Type.values()[PacketUtils.getInt(buffer)])
                .serverName(PacketUtils.getString(buffer))
                .address(new InetSocketAddress(
                        PacketUtils.getString(buffer),
                        PacketUtils.getInt(buffer))
                ).build();
    }
}
