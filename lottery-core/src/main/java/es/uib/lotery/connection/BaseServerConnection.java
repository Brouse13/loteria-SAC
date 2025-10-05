package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;
import es.uib.lotery.packet.PacketBuilder;
import es.uib.lotery.packet.PacketHandleRegistry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The {@code BaseServerConnection} class provides a low-level server-side
 * socket implementation for receiving and responding to {@link BasePacket} objects.
 * <p>
 * It implements {@link ServerConnection} and manages a non-blocking {@link ServerSocketChannel}
 * along with a {@link Selector} to handle multiple client connections asynchronously.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Start and bind a server socket on a specified address.</li>
 *     <li>Accept incoming client connections.</li>
 *     <li>Read packets from clients, handle them using {@link PacketHandleRegistry}, and reply if necessary.</li>
 *     <li>Stop the server and release all resources gracefully.</li>
 * </ul>
 *
 * <p>Logging is used extensively for tracking server events, connections, and errors.</p>
 *
 * @see ServerConnection
 * @see BasePacket
 * @see PacketHandleRegistry
 * @see PacketBuilder
 */
public class BaseServerConnection implements ServerConnection {

    /** Logger for server events and errors. */
    private static final Logger logger = Logger.getLogger(BaseServerConnection.class.getName());

    /** The server socket channel used for accepting client connections. */
    private ServerSocketChannel serverSocketChannel;

    /** Registry responsible for handling incoming packets. */
    private final PacketHandleRegistry handleRegistry;

    /** Selector used for asynchronous channel management. */
    private Selector selector;

    /** Buffer for reading and writing packet data. */
    private ByteBuffer buffer;

    /**
     * Creates a new {@code BaseServerConnection} with the provided {@link PacketHandleRegistry}.
     *
     * @param handleRegistry the registry used to handle incoming packets
     */
    public BaseServerConnection(PacketHandleRegistry handleRegistry) {
        this.handleRegistry = handleRegistry;
    }

    /**
     * Starts the server by binding it to the specified address and preparing
     * it for accepting connections.
     *
     * @param address the host and port to bind the server
     * @return {@code true} if the server was successfully started, {@code false} otherwise
     */
    @Override
    public boolean start(InetSocketAddress address) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            buffer = ByteBuffer.allocate(256);

            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            logger.warning("[SERVER] Error: %s".formatted(e.getMessage()));
            return false;
        }

        logger.config("[SERVER] Starting server on %s:%d...".formatted(address.getHostName(), address.getPort()));
        return true;
    }

    /**
     * Stops the server and closes all associated resources, including
     * the server socket channel, selector, and buffer.
     */
    @Override
    public void stop() {
        try {
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                logger.config("[SERVER] Closing server");
                serverSocketChannel.close();
            }

            if (selector != null && selector.isOpen()) {
                logger.config("[SERVER] Selector closed");
                selector.close();
            }

            buffer = null;
            logger.config("[SERVER] Server closed");

        } catch (IOException e) {
            logger.warning("[SERVER] Error: %s".formatted(e.getMessage()));
        }
    }

    /**
     * Listens for incoming connections and messages in an infinite loop.
     * <p>
     * Uses a {@link Selector} to handle multiple client connections asynchronously.
     * Each packet received is processed using the {@link PacketHandleRegistry}.
     * </p>
     *
     * @throws IOException if an I/O error occurs while listening
     */
    @Override
    public void listen() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) register();
                if (key.isReadable()) reply(key);

                iterator.remove();
            }
        }
    }

    /**
     * Registers a new client connection by accepting it and configuring
     * the channel for reading.
     *
     * @throws IOException if an I/O error occurs during registration
     */
    private void register() throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        logger.config("[SERVER] %s connected".formatted(client.getRemoteAddress()));
    }

    /**
     * Reads a packet from the client, processes it using the {@link PacketHandleRegistry},
     * and replies if there are response packets.
     *
     * @param key the {@link SelectionKey} associated with the readable client channel
     */
    private void reply(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            buffer.clear();
            int bytes = client.read(buffer);
            buffer.flip();

            if (bytes == -1) {
                logger.config("[SERVER] %s close connection".formatted(client.getRemoteAddress()));
                client.close();
                return;
            }

            if (bytes == 0) return;

            BasePacket packet = PacketBuilder.INSTANCE.buildPacket(buffer);
            logger.config("[SERVER] <-- %s (%s)".formatted(client.getRemoteAddress(), packet));

            List<BasePacket> responses = handleRegistry.handlePacket(packet);

            if (responses == null || responses.isEmpty()) return;

            for (BasePacket response : responses) {
                buffer.clear();
                response.encode(buffer);
                buffer.flip();
                client.write(buffer);
            }
        } catch (IOException e) {
            logger.warning("[SERVER] Error: %s".formatted(e.getMessage()));
            this.stop();
        }
    }
}
