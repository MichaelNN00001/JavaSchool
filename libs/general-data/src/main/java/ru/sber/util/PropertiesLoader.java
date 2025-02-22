package ru.sber.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

    /**
     * метод загружает конфигурацию Kafka из файла в объект
     * @param configFileName - имя файла конфигурации
     * @return configuration - экземпляр класса Properties
     */
    public static Properties loadProperties(String configFileName) {
        Properties configuration = new Properties();
        try {
            InputStream inputStream = PropertiesLoader.class
                    .getClassLoader()
                    .getResourceAsStream(configFileName);
            configuration.load(inputStream);
            inputStream.close();
        } catch (Exception ex) {
            log.error("PropertiesLoader.loadProperties: {}", ex.getMessage());
        }
        return configuration;
    }
}
