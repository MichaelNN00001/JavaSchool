package ru.sber;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import ru.sber.config.KafkaConfig;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;
import ru.sber.service.ConsumerService;
import ru.sber.service.StorageService;
import ru.sber.storage.SenderStorage;
import ru.sber.util.DateTimeToSecond;
import ru.sber.util.RandomValues;

import java.time.LocalDateTime;

public class ConfirmConsumerTest {

    /**
     * После заполнения в хранилище мапы unchecked консьюмер считывает из кафки сообщение
     * (необходимо направить его в топик confirmTopic)
     * {
     * 	"key": "1736943700",
     * 	"ids": [
     * 		"ebc0de57-1c5e-4345-bfeb-1b9348635f41",
     * 		"ebc0de57-1c5e-4345-bfeb-1b9348635f43"
     * 	]
     * }
     *
     */
    @Test
    public void confirmConsumerTest() {

        StorageService storageService = new StorageService();
        SenderStorage senderStorage = storageService.getSenderStorage();
        uncheckedPrepare(senderStorage);

        KafkaConsumer<String, Object> confirmKafkaConsumer =
                new KafkaConsumer<>(KafkaConfig.getKafkaProperties("confirm-consumer.properties"));

        ConsumerService service = new ConsumerService(
                "confirm-consumer.properties", storageService, confirmKafkaConsumer);
        service.listen(ConfirmData.class);

    }

    public void uncheckedPrepare(SenderStorage senderStorage) {

        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 12, 30, 0);
        LocalDateTime newDateTime;
        Transaction transaction;
        newDateTime = dateTime.minusMinutes(20);
        transaction = new Transaction(
                "ebc0de57-1c5e-4345-bfeb-1b9348635f41",
                TransactionType.OPERATION_1,
                RandomValues.getBigDecimalAmound(100000),
                "1234567890",
                newDateTime);
        senderStorage.saveUnchecked(DateTimeToSecond.getDateTimeInSeconds(newDateTime), transaction);
        newDateTime = dateTime.minusMinutes(15);
        transaction = new Transaction(
                "ebc0de57-1c5e-4345-bfeb-1b9348635f42",
                TransactionType.OPERATION_1,
                RandomValues.getBigDecimalAmound(100000),
                "1234567890",
                newDateTime);
        senderStorage.saveUnchecked(DateTimeToSecond.getDateTimeInSeconds(newDateTime), transaction);
        newDateTime = dateTime.minusMinutes(10);
        transaction = new Transaction(
                "ebc0de57-1c5e-4345-bfeb-1b9348635f43",
                TransactionType.OPERATION_1,
                RandomValues.getBigDecimalAmound(100000),
                "1234567890",
                newDateTime);
        senderStorage.saveUnchecked(DateTimeToSecond.getDateTimeInSeconds(newDateTime), transaction);
        newDateTime = dateTime.minusMinutes(5);
        transaction = new Transaction(
                "ebc0de57-1c5e-4345-bfeb-1b9348635f44",
                TransactionType.OPERATION_1,
                RandomValues.getBigDecimalAmound(100000),
                "1234567890",
                newDateTime);
        senderStorage.saveUnchecked(DateTimeToSecond.getDateTimeInSeconds(newDateTime), transaction);

    }
}
