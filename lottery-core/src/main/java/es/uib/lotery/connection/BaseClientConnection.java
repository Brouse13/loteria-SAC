package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;
import es.uib.lotery.packet.PacketBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class BaseClientConnection implements ClientConnection {
    private static final Logger logger = Logger.getLogger(BaseClientConnection.class.getName());
    private SocketChannel client;

    private ByteBuffer buffer;

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

    @Override
    public boolean send(BasePacket packet, Consumer<BasePacket> responseCallback) {
        try {
            buffer.clear();
            packet.encode(buffer);
            buffer.flip();

            int bytes = client.write(buffer);

            logger.config("[CLIENT] Packet 0x%04X sent with %d bytes".formatted(packet.hashCode(), bytes));

            // No callback provided
            if (responseCallback == null) { return true; }

            responseCallback.accept(getPacket());

        } catch (IOException e) {
            logger.warning("[CLIENT] Error: %s".formatted(e.getMessage()));
            return false;
        }

        return true;
    }

    private BasePacket getPacket() {
        try {
            buffer.clear();
            int read = client.read(buffer);
            buffer.flip();

            // Serialize packet
            BasePacket basePacket = PacketBuilder.INSTANCE.buildPacket(buffer);

            logger.config("[CLIENT] Packet 0x%04X received %d bytes".formatted(basePacket.hashCode(), read));

            return basePacket;

        } catch (IOException e) {
            logger.warning("[CLIENT] Error: %s".formatted(e.getMessage()));
        }

        return null;
    }
}
