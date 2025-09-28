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

public class BaseServerConnection implements ServerConnection {
    private ServerSocketChannel serverSocketChannel;
    private final PacketHandleRegistry handleRegistry;
    private Selector selector;
    private ByteBuffer buffer;

    public BaseServerConnection(PacketHandleRegistry handleRegistry) {
        this.handleRegistry = handleRegistry;
    }

    @Override
    public boolean start(InetSocketAddress address) {
        try {
            // Inicialización del canal de socket y buffer
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            buffer = ByteBuffer.allocate(256);

            // Conexión con el host en el puerto dado
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            System.out.printf("[SERVER] Error: %s\n", e.getMessage());
            return false;
        }

        // Conexión exitosa
        System.out.printf("[SERVER] Starting server on %s:%d...\n", address.getHostName(), address.getPort());
        return true;
    }

    @Override
    public void stop() {
        try {
            // Cierra el canal del servidor si está abierto
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                System.out.println("[SERVER] Closing server");
                serverSocketChannel.close();
            }

            // Cierra el selector si está abierto
            if (selector != null && selector.isOpen()) {
                System.out.println("[SERVER] Closing selector");
                selector.close();
            }

            // Limpia el buffer
            buffer = null;

            System.out.println("[SERVER] Server closed");

        } catch (IOException e) {
            System.out.printf("[SERVER] Error: %s\n", e.getMessage());
        }
    }

    @Override
    public void listen() throws IOException {
        while (true) {

            // Obtención de las claves de selección que representan los canales listos
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            // Iteración sobre las claves seleccionadas
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // Si la clave representa una nueva conexión, se registra
                if (key.isAcceptable()) register();

                // Si la clave es legible, procesa el mensaje del cliente
                if (key.isReadable()) reply(key);

                iterator.remove();
            }
        }
    }

    private void register() throws IOException {

        // Accept socket in read mode
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        System.out.printf("[SERVER] %s connected\n", client.getRemoteAddress());
    }

    private void reply(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            buffer.clear();
            int bytes = client.read(buffer);

            if (bytes == -1) {
                System.out.printf("[SERVER] %s close connection\n", client.getRemoteAddress());
                client.close();
                return;
            }

            if (bytes == 0) return;

            BasePacket packet = PacketBuilder.INSTANCE.buildPacket(buffer);
            System.out.printf("[SERVER] <-- %s (%s)\n", client.getRemoteAddress(), packet);

            List<BasePacket> responses = handleRegistry.handlePacket(packet);

            // No responses
            if (responses == null || responses.isEmpty()) return;

            // Reply the client
            for (BasePacket response : responses) {
                buffer.clear();
                response.encode(buffer);
                buffer.flip();
                client.write(buffer);
            }
        } catch (IOException e) {
            this.stop();
        }
    }
}
