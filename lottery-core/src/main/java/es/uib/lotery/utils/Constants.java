package es.uib.lotery.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.function.Supplier;

@UtilityClass
public class Constants {
    private final Random rand = new Random();

    public static final String DNS_HOST = "192.168.1.174";
    public static final int DNS_PORT = 8080;

    public static final String SERVER_HOST = "192.168.1.174";
    public static final int SERVER_PORT = 3030;

    public static final int CLIENT_RETRY_SECONDS = 3;

    public static Supplier<Integer> RANDOM_NUMBER = () -> rand.nextInt(10) + 1;
}
