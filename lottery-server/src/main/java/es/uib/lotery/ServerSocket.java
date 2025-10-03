package es.uib.lotery;

import static es.uib.lotery.utils.Constants.*;

public class ServerSocket {
    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.startServer(SERVER_HOST, SERVER_PORT);
        } catch (Exception e) {
            server.disconnectServer();
        }
    }
}

