package ru.sber;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProducerServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private Future<RecordMetadata> future;

    private ProducerService producerService;

    @BeforeEach
    public void setUp() {
        Properties properties = new Properties();
        properties.setProperty("topic", "test-topic");
        producerService = new ProducerService("producer.properties", storageService);
    }

    @Test
    public void testSendTransaction() throws ExecutionException, InterruptedException {
        producerService = new ProducerService("producer.properties", storageService);
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.OPERATION_1);
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(new BigDecimal("12345.67"));
        transaction.setAccount("test-account");
        transaction.setId(UUID.randomUUID().toString());

        producerService.send(transaction);

        verify(storageService).senderSaveUncheked(anyLong(), eq(transaction));
    }

    @Test
    public void testSendWrongClass() {
        Object wrongObject = new Object();

        assertThrows(RuntimeException.class, () -> producerService.send(wrongObject));
    }
}
