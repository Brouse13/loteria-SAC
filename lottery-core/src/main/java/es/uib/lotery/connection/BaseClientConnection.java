package es.uib.lotery.connection;

import es.uib.lotery.packet.BasePacket;
import es.uib.lotery.packet.PacketBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class BaseClientConnection implements ClientConnection {
    private SocketChannel client;

    private ByteBuffer buffer;

    @Override
    public boolean connect(InetSocketAddress address) {
        try {
            System.out.printf("[CLIENT] Trying to connect to %s:%d\n", address.getHostName(), address.getPort());

            client = SocketChannel.open(address);
            buffer = ByteBuffer.allocate(256);

        } catch (IOException e) {
            System.out.printf("[CLIENT] Error: %s\n", e.getMessage());
            return false;
        }

        System.out.println("[CLIENT] Connection successfully established");
        return true;
    }

    @Override
    public void disconnect() {
        try {
            if (client != null && client.isOpen()) {
                System.out.println("[CLIENT] Closing connection");
                client.close();
            }

            buffer = null;
            System.out.println("[CLIENT] Connection closed");

        } catch (IOException e) {
            System.out.printf("[CLIENT] Error: %s\n", e.getMessage());
        }
    }

    @Override
    public boolean send(BasePacket packet, Consumer<BasePacket> responseCallback) {
        try {
            buffer.clear();
            packet.encode(buffer);
            buffer.flip();

            int bytes = client.write(buffer);

            System.out.printf("[CLIENT] Packet 0x%04X sent with %d bytes\n",
                    packet.getId(), bytes);

            // No callback provided
            if (responseCallback == null) { return true; }

            responseCallback.accept(getPacket());

        } catch (IOException e) {
            System.out.printf("[CLIENT] Error: %s\n", e.getMessage());
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

            System.out.printf("[CLIENT] Packet 0x%04X received %d bytes\n",
                    basePacket.getId(), read);

            return basePacket;

        } catch (IOException e) {
            System.out.printf("[CLIENT] Error: %s\n", e.getMessage());
        }

        return null;
    }
}
