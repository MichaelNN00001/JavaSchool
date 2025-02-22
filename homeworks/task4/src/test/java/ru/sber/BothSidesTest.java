package ru.sber;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import ru.sber.config.KafkaConfig;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ConsumerService;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;
import ru.sber.tasks.SenderMainProducerTask;
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

public class BothSidesTest {

    StorageService storageService = new StorageService();
    @Test
    public void bothSidesTest() throws InterruptedException {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        ProducerService mainProducerService = new ProducerService(
                "main-producer.properties", storageService);
        ProducerService confirmProducerService = new ProducerService(
                "confirm-producer.properties", storageService);

        KafkaConsumer<String, Object> kafkaConsumer =
                new KafkaConsumer<>(KafkaConfig.getKafkaProperties("main-consumer.properties"));
        // запуск потребителя транзакций на стороне reciever
        executorService.execute(() -> new ConsumerService(
                "main-consumer.properties", storageService, kafkaConsumer).listen(Transaction.class));
        // запуск потребителя сверки транзакций на стороне sender
        KafkaConsumer<String, Object> confirmKafkaConsumer =
                new KafkaConsumer<>(KafkaConfig.getKafkaProperties("confirm-consumer.properties"));
        executorService.execute(() -> new ConsumerService(
                "confirm-consumer.properties", storageService, confirmKafkaConsumer).listen(ConfirmData.class));

        // время на ожидание отработки транзакций. Все транзакции, поступившие раньше от настоящего момента
        // на этот промежуток времени должны проходить процедуру подтверждения получения
        final Long waitingTimeSec = 20L;

        scheduledExecutorService.scheduleAtFixedRate(
                this::prepareSomeTransactions, 0, 6, TimeUnit.SECONDS);

        // запуск отправки транзакций на стороне sender
        scheduledExecutorService.scheduleAtFixedRate(
                new SenderMainProducerTask(mainProducerService, storageService),
                5, 5, TimeUnit.SECONDS);

        // запуск отправки подтверждений на стороне reciever
        scheduledExecutorService.scheduleAtFixedRate(
                new RecieverProducerForConfirmTask(confirmProducerService, waitingTimeSec, storageService),
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
            storageService.senderSaveStartSend(DateTimeToSecond.getDateTimeInSeconds(dateTimeNew), transaction);
        }
    }
}
