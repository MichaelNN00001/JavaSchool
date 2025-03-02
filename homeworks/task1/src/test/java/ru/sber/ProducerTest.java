package ru.sber;

import org.junit.Test;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProducerTest {

    @Test
    public void testProducer() {

        StorageService storageService = new StorageService();
        ProducerService producerService = new ProducerService("producer.properties", storageService);
        Transaction transaction1 = new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.OPERATION_1,
                new BigDecimal("12345.67"),
                "1234567890",
                LocalDateTime.now());
        producerService.send(transaction1);
        Transaction transaction2 = new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.OPERATION_3,
                new BigDecimal("6665544.00"),
                "34234234",
                LocalDateTime.now());
        producerService.send(transaction2);
        Transaction transaction3 = new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.OPERATION_2,
                new BigDecimal("34534.90"),
                "678678678",
                LocalDateTime.now());
        producerService.send(transaction3);
        Transaction transaction4 = new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.OPERATION_1,
                new BigDecimal("12.44"),
                "7070707",
                LocalDateTime.now());
        producerService.send(transaction4);
    }
}
