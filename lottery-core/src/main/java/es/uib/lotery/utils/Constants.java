package es.uib.lotery.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.function.Supplier;

@UtilityClass
public class Constants {

    static {
        configuration = YamlLoader.load("configuration.yaml", Configuration.class);
    }
    private final Random rand = new Random();

    private static final Configuration configuration;
    public static final String DNS_HOST = configuration.getDnsHost();
    public static final int DNS_PORT = configuration.getDnsPort();

    public static final String SERVER_HOST = configuration.getServerHost();
    public static final int SERVER_PORT = configuration.getServerPort();

    public static final int CLIENT_RETRY_SECONDS = configuration.getRetryInSeconds();

    public static Supplier<Integer> RANDOM_NUMBER = () -> rand.nextInt(10) + 1;
}
