package es.uib.lotery;

import es.uib.lotery.packet.DNSRequestPacket;
import es.uib.lotery.packet.SorteoRequestPacket;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.*;

public class ClientSocket {
    private static final Logger logger = Logger.getLogger(ClientSocket.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        new ClientSocket().start();
    }

    private void start() {
        AtomicReference<InetSocketAddress> serverAddress = new AtomicReference<>();
        Client client = new Client();

        if (client.connectToServer(DNS_HOST, DNS_PORT)) {
            DNSRequestPacket packet = new DNSRequestPacket();
            client.requestAddress(packet, serverAddress::set);
            client.disconnect();
        }

        final Random random = new Random();
        final InetSocketAddress address = serverAddress.get();

        scheduler.scheduleAtFixedRate(() -> {
            if (client.connectToServer(address.getHostName(), address.getPort())) {
                int number = RANDOM_NUMBER.get();
                client.pedirSorteo(SorteoRequestPacket.builder().requestNumber(number).build());

                if (client.hasWin()) logger.config("Has ganado con el numero: %d".formatted(number));

                client.disconnect();
            }
        },0, random.nextInt(CLIENT_RETRY_SECONDS), TimeUnit.SECONDS);
    }
}
