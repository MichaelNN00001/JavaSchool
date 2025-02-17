package ru.sber;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import ru.sber.config.KafkaConfig;
import ru.sber.model.Transaction;
import ru.sber.service.ConsumerService;
import ru.sber.service.StorageService;

public class ConsumerTest {

    @Test
     public void testConsumer() {

        KafkaConsumer<String, Object> kafkaConsumer =
                new KafkaConsumer<>(KafkaConfig.getKafkaProperties("consumer.properties"));
        StorageService storageService = new StorageService();
        ConsumerService service = new ConsumerService("consumer.properties", storageService, kafkaConsumer);
        service.listen(Transaction.class);
    }
}
