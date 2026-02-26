package es.uib.lotery;

import es.uib.lotery.utils.LoggingUtils;

import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.*;

/**
 * The {@code ServerSocket} class is the entry point for starting the main
 * lottery server that handles incoming lottery draw requests from sellers.
 * <p>
 * It creates an instance of {@link Server} and binds it to the host and port
 * defined in {@link es.uib.lotery.utils.Constants}. The server listens for
 * incoming packets from sellers and processes draw requests to determine
 * winners.
 * </p>
 *
 * <p>Typical usage (command line):</p>
 * <pre>{@code
 * java -jar lottery-server.jar
 * }</pre>
 *
 * <p>If an exception occurs during startup or execution, the server is
 * gracefully disconnected and a warning is logged.</p>
 *
 * @author Brouse13
 * @version 1.0
 * @see Server
 * @see es.uib.lotery.utils.Constants
 */
public class ServerSocket {
    /** Logger used for recording server events and errors. */
    private static final Logger logger = Logger.getLogger(ServerSocket.class.getName());

    /**
     * Application entry point.
     * <p>
     * Initializes and starts the {@link Server} on the predefined host and port.
     * If any errors occur during startup, the server is stopped and the error
     * message is logged.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        LoggingUtils.loadConfiguration();
        Server server = new Server();

        try {
            server.startServer(SERVER_HOST, SERVER_PORT);
        } catch (Exception e) {
            server.disconnectServer();
            logger.warning("Server disconnected - %s".formatted(e.getMessage()));
        }
    }
}
