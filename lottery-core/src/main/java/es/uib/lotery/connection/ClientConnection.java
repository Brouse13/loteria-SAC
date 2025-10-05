package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * Represents a client-side connection capable of connecting to a server,
 * sending packets, and disconnecting.
 * <p>
 * Implementations may use blocking or non-blocking I/O. The interface defines
 * the expected contract for sending {@link BasePacket} objects and optionally
 * receiving responses asynchronously.
 * </p>
 *
 * @see BaseClientConnection
 * @see BasePacket
 */
public interface ClientConnection {

    /**
     * Establishes a connection to the specified server address.
     *
     * @param address the server {@link InetSocketAddress} to connect to
     * @return {@code true} if the connection was successfully established,
     *         {@code false} otherwise
     */
    boolean connect(InetSocketAddress address);

    /**
     * Disconnects the client from the server and releases any associated resources.
     */
    void disconnect();

    /**
     * Sends a {@link BasePacket} to the server.
     * <p>
     * If a {@code responseCallback} is provided, it will be invoked with the
     * server's response once received.
     * </p>
     *
     * @param packet the {@link BasePacket} to send
     * @param responseCallback a callback function to handle the response, may be {@code null}
     * @return {@code true} if the packet was successfully sent, {@code false} if an error occurred
     */
    boolean send(BasePacket packet, Consumer<BasePacket> responseCallback);
}
