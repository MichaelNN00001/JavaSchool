package ru.sber.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.config.KafkaConfig;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.storage.SenderStorage;
import ru.sber.util.DateTimeToSecond;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsumerService {

    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();// 1

    private final String topic;
    private final Properties properties;
    private final StorageService storageService;
    private final KafkaConsumer<String, Object> kafkaConsumer;

    public ConsumerService(
            String propertiesFileName, StorageService storageService, KafkaConsumer<String, Object> kafkaConsumer) {
        this.properties = KafkaConfig.getKafkaProperties(propertiesFileName);
        this.storageService = storageService;
        this.kafkaConsumer = kafkaConsumer;
        this.topic = properties.getProperty("topic");
    }

    public <T> void listen(T clazz) {

        log.info("ConsumerService.listen start: " + LocalDateTime.now()
                + " Thread: " + Thread.currentThread().getName());
        try (kafkaConsumer) {
            kafkaConsumer.subscribe(List.of(topic));
            log.info("counter = 0!");
            while (true) {
                ConsumerRecords<String, Object> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Object> record : consumerRecords) {
                    log.info("topic: {}; offset: {}; partition: {}; groupId: {}; value: {}",
                            record.topic(), record.offset(), record.partition(),
                            kafkaConsumer.groupMetadata().groupId(), record.value()
                    );
                    String className = ((Class<?>) clazz).getName();
                    if (className.contains("Transaction")) {    // консьюмер на принимающей стороне основного потока
                        Transaction transaction = (Transaction) record.value();
                        storageService.recieverSaveUnchecked(
                                DateTimeToSecond.getDateTimeInSeconds(transaction.getDate()), transaction);
                    } else if (className.contains("ConfirmData")) { // консьюмер на принимающей стороне обратного потока
                        ConfirmData confirmData = (ConfirmData) record.value();
                        int sizeOfForSendAgain = storageService.senderConfirm(confirmData);
                        if (sizeOfForSendAgain > 0) {
                            log.info("Обнаружены не прошедшую проверку транзакции в количестве {} шт.",
                                    sizeOfForSendAgain);
                            sendAgain();
                        } else log.info("Все транзакции успешно прошли проверку");
                    } else
                        throw new RuntimeException("wrong class name: " + className);
                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1, "no metadata"));
                    kafkaConsumer.commitAsync(currentOffsets, null);
                    log.info("Records commited");
                }
            }
        }catch (WakeupException e) {
            System.out.println("Shutting down...");
        } catch (Exception e) {
            log.error("Error processing messages {}", e.getMessage());
            throw new SerializationException(e);
        } finally {
            kafkaConsumer.close();
        }
    }

    public void stop() {
        kafkaConsumer.wakeup();
    }
    private void sendAgain() {

        SenderStorage senderStorage = storageService.getSenderStorage();
        ProducerService producerService = new ProducerService("main-producer.properties", storageService);
        List<Transaction> transactionList = senderStorage.getForSendAgainValueList();
        for (Transaction transaction : transactionList)
            producerService.send(transaction);
    }
}
