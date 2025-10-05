package es.uib.lotery.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class YamlLoader {
    public static <T> T load(String fileName, Class<T> clazz) {
        Yaml yaml = new Yaml();

        System.out.printf("Loading %s...\n", fileName);

        try {
            File file = new File(fileName);
            InputStream input = file.exists() && file.isFile() ?
                    new FileInputStream(file) :
                    YamlLoader.class.getClassLoader().getResourceAsStream(fileName);

            if (input == null) throw new RuntimeException("Cannot find config file: " + fileName);

            return yaml.loadAs(input, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
    }
}
