package es.uib.lotery.connection;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface ServerConnection {
    boolean start(InetSocketAddress address);

    void stop();

    void listen() throws IOException;
}
