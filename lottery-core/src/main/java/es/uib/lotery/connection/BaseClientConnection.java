package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;
import es.uib.lotery.packet.PacketBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * The {@code BaseClientConnection} class provides a low-level client-side
 * socket connection for sending and receiving {@link BasePacket} objects.
 * <p>
 * This class implements {@link ClientConnection} and manages a non-blocking
 * {@link SocketChannel}, encoding packets into a {@link ByteBuffer} for transmission.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Establish a TCP connection to a given server address.</li>
 *     <li>Send serialized {@link BasePacket} objects.</li>
 *     <li>Receive responses from the server and optionally handle them via a callback.</li>
 *     <li>Gracefully disconnect and release resources.</li>
 * </ul>
 *
 * <p>Logging is used extensively to track connection and packet events.</p>
 *
 * @see ClientConnection
 * @see BasePacket
 * @see PacketBuilder
 */
public class BaseClientConnection implements ClientConnection {

    /** Logger for client events and error reporting. */
    private static final Logger logger = Logger.getLogger(BaseClientConnection.class.getName());

    /** The underlying socket channel for communication. */
    private SocketChannel client;

    /** Buffer used for encoding and decoding packets. */
    private ByteBuffer buffer;

    /**
     * Connects to the specified server address via a {@link SocketChannel}.
     *
     * @param address the server address to connect to
     * @return {@code true} if the connection is successfully established, {@code false} otherwise
     */
    @Override
    public boolean connect(InetSocketAddress address) {
        try {
            logger.config("[CLIENT] Trying to connect to %s:%d ".formatted(address.getHostName(), address.getPort()));

            client = SocketChannel.open(address);
            buffer = ByteBuffer.allocate(256);

        } catch (IOException e) {
            logger.warning("[CLIENT] Error: %s".formatted(e.getMessage()));
            return false;
        }

        logger.config("[CLIENT] Connection successfully established");
        return true;
    }

    /**
     * Disconnects from the server and releases the {@link SocketChannel} and buffer.
     * Logs both normal closure and any errors encountered.
     */
    @Override
    public void disconnect() {
        try {
            if (client != null && client.isOpen()) {
                logger.config("[CLIENT] Closing connection...");
                client.close();
            }

            buffer = null;
            logger.config("[CLIENT] Connection closed");

        } catch (IOException e) {
            logger.warning("[CLIENT] Error: %s".formatted(e.getMessage()));
        }
    }

    /**
     * Sends a {@link BasePacket} to the connected server and optionally invokes a response callback.
     *
     * @param packet the packet to send
     * @param responseCallback a {@link Consumer} to handle the response, may be {@code null}
     * @return {@code true} if the packet was sent successfully, {@code false} if an error occurred
     */
    @Override
    public boolean send(BasePacket packet, Consumer<BasePacket> responseCallback) {
        try {
            buffer.clear();
            packet.encode(buffer);
            buffer.flip();

            int bytes = client.write(buffer);

            logger.config("[CLIENT] Packet 0x%04X sent with %d bytes".formatted(packet.hashCode(), bytes));

            // No callback provided
            if (responseCallback == null) {
                return true;
            }

            responseCallback.accept(getPacket());

        } catch (IOException e) {
            logger.warning("[CLIENT] Error: %s".formatted(e.getMessage()));
            return false;
        }

        return true;
    }

    /**
     * Reads a packet from the server and deserializes it into a {@link BasePacket}.
     *
     * @return the received {@link BasePacket}, or {@code null} if an error occurred
     */
    private BasePacket getPacket() {
        try {
            buffer.clear();
            int read = client.read(buffer);
            buffer.flip();

            BasePacket basePacket = PacketBuilder.INSTANCE.buildPacket(buffer);

            logger.config("[CLIENT] Packet 0x%04X received %d bytes".formatted(basePacket.hashCode(), read));

            return basePacket;

        } catch (IOException e) {
            logger.warning("[CLIENT] Error: %s".formatted(e.getMessage()));
        }

        return null;
    }
}
