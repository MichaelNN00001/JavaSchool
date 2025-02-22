package ru.sber;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.config.KafkaConfig;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.service.ConsumerService;
import ru.sber.service.StorageService;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumerServiceTest.class);

    @Mock
    private StorageService storageService;

    private ConsumerService consumerService;
    private MockConsumer<String, Object> mockConsumer;
    private KafkaConsumer<String, Object> kafkaConsumer;

    @BeforeEach
    public void setUp() {
        mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        kafkaConsumer =
                new KafkaConsumer<>(KafkaConfig.getKafkaProperties("consumer.properties"));
        consumerService = new ConsumerService("consumer.properties", storageService, kafkaConsumer);
    }

    @Test
    public void testListenWithTransaction() {

        mockConsumer.schedulePollTask(() ->
                mockConsumer.addRecord(new ConsumerRecord<>("test-topic", 0, 0L, "key", new Transaction())));
        mockConsumer.schedulePollTask(() -> consumerService.stop());

        HashMap<TopicPartition, Long> startingOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition("test-topic", 0);
        startingOffsets.put(tp, 0L);
        mockConsumer.updateBeginningOffsets(startingOffsets);

        Transaction transaction = new Transaction();
        ConsumerRecord<String, Object> record =
                new ConsumerRecord<>("test-topic", 0, 0L, "key", transaction);
        ConsumerRecords<String, Object> consumerRecords =
                new ConsumerRecords<>(Map.of(new TopicPartition("test-topic", 0), List.of(record)));

        KafkaConsumer<String, Object> mockedKafkaConsumer = mock(KafkaConsumer.class);
        when(mockedKafkaConsumer.poll(any(Duration.class))).thenReturn(consumerRecords);

        consumerService.listen(Transaction.class);
        assertTrue(mockConsumer.closed());

        verify(storageService).recieverSaveUnchecked(anyLong(), eq(transaction));
    }

    @Test
    public void testListenWithWrongClass() {
        Object wrongObject = new Object();
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", wrongObject);
        ConsumerRecords<String, Object> consumerRecords = new ConsumerRecords<>(Map.of(new TopicPartition("test-topic", 0), List.of(record)));

        mockConsumer.schedulePollTask(() ->
                mockConsumer.addRecord(new ConsumerRecord<>("test-topic", 0, 0L, "key", wrongObject)));
        mockConsumer.schedulePollTask(() -> consumerService.stop());

        HashMap<TopicPartition, Long> startingOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition("test-topic", 0);
        startingOffsets.put(tp, 0L);
        mockConsumer.updateBeginningOffsets(startingOffsets);

        KafkaConsumer<String, Object> mockedKafkaConsumer = mock(KafkaConsumer.class);
        when(mockedKafkaConsumer.poll(any(Duration.class))).thenReturn(consumerRecords);

        assertThrows(RuntimeException.class, () -> consumerService.listen(wrongObject.getClass()));
    }
}