package sbp.school.kafka.config;

import sbp.school.kafka.util.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;

public class KafkaConfig {

    private static final String PROPERTIES_FILE = "kafka.properties";

    /**
     * метод инициирует загрузку конфигурации в файл
     * @return - экземпляр класса Properties
     */
    public static Properties getKafkaProperties() {

        return PropertiesLoader.loadProperties(PROPERTIES_FILE);
    }

}
