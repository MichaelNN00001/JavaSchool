package ru.sber.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.Transaction;

import java.io.IOException;


public class TransactionDeserializer implements Deserializer<Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionDeserializer.class);

    @Override
    /**
     * метод десериализует объект после его получения в Kafka
     * @param topic - топик
     * @param bytes - массив байт-кода
     * @return - десериализуемый объект
     */
    public Transaction deserialize(String topic, byte[] bytes) {

        ObjectMapper objectMapper = new ObjectMapper();
        if (bytes == null || bytes.length == 0) {
            String errorMessage = "Отсутствуют данные в сообщении из топика: " + topic;
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        } else {
            try {
                return objectMapper.readValue(bytes, Transaction.class);
            } catch (IOException e) {
                log.error("Ошибка десериализации в Transaction: {}, topic {}", e.getMessage(), topic);
                throw new RuntimeException(e);
            }
        }
    }
}
