package ru.sber;

import org.junit.Test;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ProducerService;
import ru.sber.storage.SenderStorage;
import ru.sber.storage.SenderStorageImpl;
import ru.sber.util.DateTimeToSecond;
import ru.sber.util.RandomValues;

import java.time.LocalDateTime;
import java.util.*;

public class MainProducerTest {

    @Test
    public void testMainProducer() {

        SenderStorage senderStorage = new SenderStorageImpl();
        ProducerService producerService = new ProducerService("main-producer.properties", senderStorage);
        List<Integer> secondsToPast = Arrays.asList(12, 15, 21, 33);
        List<Integer> intervals = Arrays.asList(20, 4);
        TransactionType[] transactionTypes = TransactionType.values();
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 12, 30, 0);

        for (Integer interval : intervals) {
            for (Integer seconds : secondsToPast) {
                int randomInt = RandomValues.getRandomInt(0, 3);
                TransactionType transactionType = transactionTypes[randomInt];
                Transaction transaction = new Transaction(
                        UUID.randomUUID().toString(),
                        transactionType,
                        RandomValues.getBigDecimalAmound(100000),
                        "1234567890",
                        dateTime.minusMinutes(interval).minusSeconds(seconds));
                producerService.send(transaction);
            }
        }
    }

    @Test
    public void rrr() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 12, 15, 0);
        Long sss = DateTimeToSecond.getDateTimeInSeconds(dateTime);
        // 1736943300
    }
}
