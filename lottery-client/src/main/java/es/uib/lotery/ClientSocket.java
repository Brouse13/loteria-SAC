package es.uib.lotery;

import es.uib.lotery.packet.DNSRequestPacket;
import es.uib.lotery.packet.SorteoRequestPacket;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static es.uib.lotery.utils.Constants.*;

public class ClientSocket {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        new ClientSocket().start();
    }

    private void start() {
        AtomicReference<InetSocketAddress> serverAddress = new AtomicReference<>();
        Client client = new Client();

        if (client.connectToServer(DNS_HOST, DNS_PORT)) {
            DNSRequestPacket packet = DNSRequestPacket.builder().serverName("seller").build();
            client.requestAddress(packet, serverAddress::set);
            client.disconnect();
        }

        final Random random = new Random();
        final InetSocketAddress address = serverAddress.get();

        scheduler.scheduleAtFixedRate(() -> {
            if (client.connectToServer(address.getHostName(), address.getPort())) {
                int number = RANDOM_NUMBER.get();
                System.out.println(number + "---------------------");
                client.pedirSorteo(SorteoRequestPacket.builder().requestNumber(number).build());

                if (client.hasWin()) {
                    System.out.printf("--------------------Has ganado con el numero: %d\n", number);
                }

                client.disconnect();
            }
        },0, random.nextInt(CLIENT_RETRY_SECONDS), TimeUnit.SECONDS);
    }
}
