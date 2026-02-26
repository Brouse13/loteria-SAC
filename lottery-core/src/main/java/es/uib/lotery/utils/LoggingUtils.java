package es.uib.lotery.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LoggingUtils {
    public static void loadConfiguration() {
        try (InputStream is = LoggingUtils.class.getClassLoader().getResourceAsStream("logging.properties")) {

            if (is == null) {
                System.err.println("logging.properties not found");
                return;
            }
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
