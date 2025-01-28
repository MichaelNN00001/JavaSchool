package ru.sber.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.config.KafkaConfig;
import ru.sber.model.Transaction;
import ru.sber.storage.SenderStorage;
import ru.sber.util.DateTimeToSecond;

import java.util.Properties;
import java.util.concurrent.Future;

public class ProducerService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

    private final Properties properties;
    private final String topic;
    private final SenderStorage senderStorage;
    public ProducerService(String propertiesFileName, SenderStorage senderStorage) {
        this.properties = KafkaConfig.getKafkaProperties(propertiesFileName);
        this.senderStorage = senderStorage;
        this.topic = properties.getProperty("topic");
   }

    /**
     * метод отправляет объект в Kafka
     * @param transaction - отправляемый объект
     */
    public void send(Transaction transaction) {

        log.info("Объект {} направляется в топик {}", transaction.toString(), topic);
        try (KafkaProducer<String, Transaction> producer = new KafkaProducer<>(properties)) {
            Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topic, transaction.getType().name(), transaction));
            RecordMetadata recordMetadata = future.get();
            senderStorage.saveUnchecked(DateTimeToSecond.getDateTimeInSeconds(transaction.getDate()), transaction);
            log.info(
                    "Успешная отправка: topic: {}, partition: {}, offset: {}",
                    recordMetadata.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset()
            );
            producer.flush();
        } catch (Throwable e) {
            log.error("Ошибка при отправке {} в {}.", transaction, topic, e);
        }
    }
}

