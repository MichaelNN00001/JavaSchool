package ru.sber;

import org.junit.Test;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ProducerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProducerTest {

    @Test
    public void testProducer() {

        ProducerService producerService = new ProducerService("producer.properties");
        Transaction transaction1 = new Transaction(
                TransactionType.OPERATION_1,
                new BigDecimal("12345.67"),
                "1234567890",
                LocalDateTime.now());
        producerService.send(transaction1);
        Transaction transaction2 = new Transaction(
                TransactionType.OPERATION_3,
                new BigDecimal("6665544.00"),
                "34234234",
                LocalDateTime.now());
        producerService.send(transaction2);
        Transaction transaction3 = new Transaction(
                TransactionType.OPERATION_2,
                new BigDecimal("34534.90"),
                "678678678",
                LocalDateTime.now());
        producerService.send(transaction3);
        Transaction transaction4 = new Transaction(
                TransactionType.OPERATION_1,
                new BigDecimal("12.44"),
                "7070707",
                LocalDateTime.now());
        producerService.send(transaction4);
    }
}
