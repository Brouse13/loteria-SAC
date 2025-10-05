package es.uib.lotery.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public class YamlLoader {
    private static final Logger logger = Logger.getLogger(YamlLoader.class.getName());
    public static <T> T load(String fileName, Class<T> clazz) {
        Yaml yaml = new Yaml();

        logger.config("Loading file %s...".formatted(fileName));

        try {
            File file = new File(fileName);
            InputStream input = file.exists() && file.isFile() ?
                    new FileInputStream(file) :
                    YamlLoader.class.getClassLoader().getResourceAsStream(fileName);

            if (input == null) logger.warning("Cannot find config file %s".formatted(fileName));

            return yaml.loadAs(input, clazz);
        } catch (Exception e) {
            logger.warning("Failed to load config: %s - %s".formatted(e.getMessage(), e.getMessage()));
            return null;
        }
    }
}
