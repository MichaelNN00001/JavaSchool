package ru.sber.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.ConfirmData;
import ru.sber.util.Validation;

import java.nio.charset.StandardCharsets;

public class ConfirmDataSerializer implements Serializer<ConfirmData> {

    private static final Logger log = LoggerFactory.getLogger(ConfirmDataSerializer.class);

    /**
     * метод сериализует объект для его дальнейшей отправки в Kafka
     * @param s - не используется в данной реализации метода
     * @param confirmData - сериализуемый объект
     * @return массив байт-кода
     */

    @Override
    public byte[] serialize(String s, ConfirmData confirmData) {
        if (confirmData != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            try {
                String value = objectMapper.writeValueAsString(confirmData);
                Validation.validateWithSchema(
                        objectMapper.readTree(value), this.getClass(), "/json/confirmdata.json");

                return value.getBytes(StandardCharsets.UTF_8);
            } catch (JsonProcessingException e) {
                log.error("Ошибка в serialize: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return new byte[0];
    }

}
