package es.uib.lotery;

import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.*;

public class ServerSocket {
    private static final Logger logger = Logger.getLogger(ServerSocket.class.getName());
    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.startServer(SERVER_HOST, SERVER_PORT);
        } catch (Exception e) {
            server.disconnectServer();
            logger.warning("Server disconnected - %s".formatted(e.getMessage()));
        }
    }
}

