package ru.sber.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.config.KafkaConfig;
import ru.sber.model.Transaction;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsumerService {

    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();// 1

    private final String topic;
    private final Properties properties;


    public ConsumerService(String propertiesFileName) {
        this.properties = KafkaConfig.getKafkaProperties(propertiesFileName);
        this.topic = properties.getProperty("topic");
    }

    public void listen() {

        int counter = 0;
        try(KafkaConsumer<String, Transaction> kafkaConsumer = new KafkaConsumer<>(properties)) {
            kafkaConsumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, Transaction> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                for(ConsumerRecord<String, Transaction> record: consumerRecords) {
                    log.info("topic: {}; offset: {}; partition: {}; groupId: {}; value: {}",
                            record.topic(), record.offset(), record.partition(),
                            kafkaConsumer.groupMetadata().groupId(), record.value()
                    );
                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1, "no metadata"));
                    counter++;
                    if (counter % 10 == 0) {
                        log.info("Records commited");
                        kafkaConsumer.commitAsync(currentOffsets, null);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing messages {}", e.getMessage());
            throw new SerializationException(e);
        }
    }
}
