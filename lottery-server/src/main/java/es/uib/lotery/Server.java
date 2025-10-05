package es.uib.lotery;

import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.RANDOM_NUMBER;

/**
 * The {@code Server} class represents the central lottery server responsible for
 * generating and validating winning numbers in the lottery system.
 * <p>
 * It listens for incoming {@link SorteoRequestPacket} packets from sellers,
 * compares the received number with the current winning number ("sorteo"),
 * and responds with a {@link SorteoResponsePacket} indicating whether the
 * player has won. When a player wins, the server automatically generates a
 * new random winning number.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * Server server = new Server();
 * server.startServer("localhost", 8080);
 * }</pre>
 *
 * @see SorteoRequestPacket
 * @see SorteoResponsePacket
 * @see BaseServerConnection
 */
@Getter
@Setter
@AllArgsConstructor
public class Server {
    /** Logger used for diagnostic and configuration messages. */
    public static final Logger logger = Logger.getLogger(Server.class.getName());

    /** Random number generator for auxiliary operations. */
    private Random rand = new Random();

    /** The underlying server connection handling incoming client requests. */
    private final BaseServerConnection connectionServer;

    /** The current winning number in the ongoing lottery draw. */
    private int sorteo;

    /**
     * Creates a new {@code Server} instance, initializes the connection registry,
     * generates the first random winning number, and registers the request handler
     * for lottery draw packets.
     */
    public Server() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.connectionServer = new BaseServerConnection(registry);

        sorteo = RANDOM_NUMBER.get();

        logger.info("Server started");
        logger.config("Sorteo - " + sorteo);

        registry.registerHandler(SorteoRequestPacket.class, this::sortearNumero);
    }

    /**
     * Handles incoming {@link SorteoRequestPacket} packets by checking if
     * the requested number matches the current winning number.
     * <p>
     * If the request number equals the current draw ({@code sorteo}), the
     * response indicates a win and a new random number is generated.
     * </p>
     *
     * @param request the received lottery request packet
     * @return a list containing a single {@link SorteoResponsePacket} representing
     *         the result of the draw (win or lose)
     */
    private List<BasePacket> sortearNumero(SorteoRequestPacket request) {
        SorteoResponsePacket response = SorteoResponsePacket.builder()
                .win(request.getRequestNumber() == sorteo)
                .build();

        logger.config("Numero sorteado %d - (%d)".formatted(request.getRequestNumber(), sorteo));

        // If the number was guessed correctly, generate a new winning number
        if (response.isWin()) sorteo = RANDOM_NUMBER.get();

        return List.of(response);
    }

    /**
     * Starts the lottery server on the specified host and port.
     *
     * @param host the hostname or IP address to bind the server
     * @param port the TCP port to listen on
     * @throws IOException if the server fails to bind or start listening
     */
    public void startServer(String host, int port) throws IOException {
        connectionServer.start(new InetSocketAddress(host, port));
        connectionServer.listen();
    }

    /**
     * Stops the server gracefully, closing all active connections.
     */
    public void disconnectServer() {
        this.connectionServer.stop();
    }
}
