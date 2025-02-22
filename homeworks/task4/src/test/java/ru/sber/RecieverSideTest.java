package ru.sber;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import ru.sber.config.KafkaConfig;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ConsumerService;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;
import ru.sber.tasks.RecieverProducerForConfirmTask;
import ru.sber.util.DateTimeToSecond;
import ru.sber.util.RandomValues;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RecieverSideTest {

    StorageService storageService = new StorageService();
    @Test
    public void recieverSideTest() throws InterruptedException {

        prepareSomeTransactions();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        ProducerService producerService = new ProducerService(
                "confirm-producer.properties", storageService);

        // запуск потребителя транзакций на стороне reciever
        KafkaConsumer<String, Object> kafkaConsumer =
                new KafkaConsumer<>(KafkaConfig.getKafkaProperties("main-consumer.properties"));
        executorService.execute(() -> new ConsumerService(
                "main-consumer.properties", storageService, kafkaConsumer).listen(Transaction.class));

        // время на ожидание отработки транзакций. Все транзакции, поступившие раньше от настоящего момента
        // на этот промежуток времени должны проходить процедуру подтверждения получения
        final Long waitingTimeSec = 20L;

        // запуск отправки подтверждений на стороне reciever
        scheduledExecutorService.scheduleAtFixedRate(
                new RecieverProducerForConfirmTask(producerService, waitingTimeSec, storageService),
                10, 12, TimeUnit.SECONDS);

        Thread.currentThread().join();


    }

    void prepareSomeTransactions() {

        LocalDateTime dateTime = LocalDateTime.now();
        TransactionType[] transactionTypes = TransactionType.values();
        List<Integer> secondsToPast = Arrays.asList(2, 10, 21, 33);
        for (Integer seconds : secondsToPast) {
            int randomInt = RandomValues.getRandomInt(0, 3);
            TransactionType transactionType = transactionTypes[randomInt];
            LocalDateTime dateTimeNew = dateTime.minusSeconds(seconds);
            Transaction transaction = new Transaction(
                    UUID.randomUUID().toString(),
                    transactionType,
                    RandomValues.getBigDecimalAmound(100000),
                    "1234567890",
                    dateTimeNew);
            storageService.recieverSaveUnchecked(DateTimeToSecond.getDateTimeInSeconds(dateTimeNew), transaction);
        }
    }
}
