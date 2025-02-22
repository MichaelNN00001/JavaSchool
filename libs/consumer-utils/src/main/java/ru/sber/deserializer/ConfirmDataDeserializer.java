package ru.sber.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.ConfirmData;
import ru.sber.util.Validation;

import java.io.IOException;
import java.text.SimpleDateFormat;


public class ConfirmDataDeserializer implements Deserializer<ConfirmData> {

    private static final Logger log = LoggerFactory.getLogger(ConfirmDataDeserializer.class);

    @Override
    /**
     * метод десериализует объект после его получения в Kafka
     * @param topic - топик
     * @param bytes - массив байт-кода
     * @return - десериализуемый объект
     */
    public ConfirmData deserialize(String topic, byte[] bytes) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
        objectMapper.registerModule(new JavaTimeModule());
        if (bytes == null || bytes.length == 0) {
            String errorMessage = "Отсутствуют данные в сообщении из топика: " + topic;
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        } else {
            try {
                log.info("Deserialization: {}", bytes);
                Validation.validateWithSchema(
                        objectMapper.readTree(bytes), this.getClass(), "/json/confirmdata.json");
                return objectMapper.readValue(bytes, ConfirmData.class);
            } catch (IOException e) {
                log.error("Ошибка десериализации в Transaction: {}, topic {}", e.getMessage(), topic);
                throw new SerializationException(e);
            }
        }
    }
}
