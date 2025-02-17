package ru.sber.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.config.KafkaConfig;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.util.DateTimeToSecond;
import ru.sber.util.RandomValues;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

    private final Properties properties;
    private final String topic;
    private final StorageService storageService;

    public ProducerService(String propertiesFileName, StorageService storageService) {
        this.properties = KafkaConfig.getKafkaProperties(propertiesFileName);
        this.storageService = storageService;
        this.topic = properties.getProperty("topic");
    }

    /**
     * метод отправляет объект в Kafka
     *
     * @param sendingObject - отправляемый объект
     */
    public <T> void send(T sendingObject) {

        log.info("Объект {} направляется в топик {}", sendingObject.toString(), topic);
        try (KafkaProducer<String, T> producer = new KafkaProducer<>(properties)) {

            String className = sendingObject.getClass().getName();
            Future<RecordMetadata> future;
            if (className.contains("Transaction")) {
                Transaction transaction = (Transaction) sendingObject;
                storageService.senderSaveUncheked(DateTimeToSecond.getDateTimeInSeconds(transaction.getDate()), transaction);
                future = producer.send(new ProducerRecord<>(
                        topic, transaction.getType().name(), sendingObject));
            } else if (className.contains("ConfirmData")) {
                int randomInt = RandomValues.getRandomInt(1, 4);
                future = producer.send(new ProducerRecord<>(
                        topic, TransactionType.valueOf("OPERATION_" + randomInt).name(), sendingObject));
            } else throw new RuntimeException("Wrong class name: " + className);
            RecordMetadata recordMetadata = future.get();
            log.info(
                    "Успешная отправка: topic: {}, partition: {}, offset: {}",
                    recordMetadata.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset()
            );
            producer.flush();
        } catch (RuntimeException | InterruptedException | ExecutionException e) {
            log.error("Ошибка при отправке {} в {}.", sendingObject, topic, e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

