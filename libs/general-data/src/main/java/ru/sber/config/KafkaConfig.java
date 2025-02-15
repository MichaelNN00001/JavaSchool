package ru.sber.config;

import ru.sber.util.PropertiesLoader;

import java.util.Properties;

public class KafkaConfig {

    /**
     * метод инициирует загрузку конфигурации в файл
     * @return - экземпляр класса Properties
     */
    public static Properties getKafkaProperties(String propertiesFileName) {

        return PropertiesLoader.loadProperties(propertiesFileName);
    }

}
