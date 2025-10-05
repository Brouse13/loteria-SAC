package es.uib.lotery.connection;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Represents a server-side connection capable of accepting client connections,
 * listening for incoming packets, and managing the server lifecycle.
 * <p>
 * Implementations may use blocking or non-blocking I/O and are responsible
 * for handling multiple clients, reading and writing packets, and closing
 * resources when the server stops.
 * </p>
 *
 * @see BaseServerConnection
 */
public interface ServerConnection {

    /**
     * Starts the server by binding it to the specified address.
     * <p>
     * Initializes the server socket and prepares it to accept incoming connections.
     * </p>
     *
     * @param address the host and port to bind the server
     * @return {@code true} if the server was successfully started, {@code false} otherwise
     */
    boolean start(InetSocketAddress address);

    /**
     * Stops the server and releases all associated resources.
     * <p>
     * After calling this method, the server will no longer accept connections or process packets.
     * </p>
     */
    void stop();

    /**
     * Listens for incoming client connections and packets.
     * <p>
     * This method typically blocks and handles all incoming client interactions.
     * Implementations are responsible for reading packets and responding appropriately.
     * </p>
     *
     * @throws IOException if an I/O error occurs while listening
     */
    void listen() throws IOException;
}
