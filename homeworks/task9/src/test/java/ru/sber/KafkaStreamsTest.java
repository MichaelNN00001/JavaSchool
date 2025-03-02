package ru.sber;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.Test;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionWithFullName;
import ru.sber.model.TransactionType;
import ru.sber.serde.JsonDeserializer;
import ru.sber.serde.JsonSerializer;
import ru.sber.serde.WrapperSerde;
import ru.sber.service.ConsumerService;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaStreamsTest {

    StorageService storageService = new StorageService();

    @Test
    public void kafkaStreamsTest() throws InterruptedException {

        fillStorageReferenceAccountFullName();
        sendSeveralTransactions();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.execute(() -> new ConsumerService(
                "consumer.properties", storageService).listen(TransactionWithFullName.class));

        kafkaStreamsLauncher();
    }

    void kafkaStreamsLauncher() throws InterruptedException {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "APPLICATION_ID");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, TransactionSerde.class.getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Transaction> source = builder.stream("testTopic");

        KStream<String, TransactionWithFullName> newTransaction =
                source.mapValues(this::getTransactionWithFullName);

        newTransaction.to("testTopic-out");

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
        Thread.sleep(60000L);
        streams.close();

    }

    private TransactionWithFullName getTransactionWithFullName(Transaction transaction) {
        TransactionWithFullName transactionWithFullName = new TransactionWithFullName();
        transactionWithFullName.setAccount(transaction.getAccount());
        transactionWithFullName.setDate(transaction.getDate());
        transactionWithFullName.setAmount(transaction.getAmount());
        transactionWithFullName.setType(transaction.getType());
        transactionWithFullName.setId(transaction.getId());
        transactionWithFullName.setFullName(storageService.getReferenceAccountFullName(transaction.getAccount()));
        return transactionWithFullName;
    }

    void sendSeveralTransactions() {

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

    void fillStorageReferenceAccountFullName() {
        storageService.saveReferenceAccountFullName("1234567890", "Ivan Ivanov");
        storageService.saveReferenceAccountFullName("34234234", "Pavel Pavlov");
        storageService.saveReferenceAccountFullName("7070707", "Nikolay Nikolov");
        storageService.saveReferenceAccountFullName("678678678", "Vladimir Vladimirov");
    }

    static public final class TransactionSerde extends WrapperSerde<Transaction> {
        public TransactionSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Transaction.class));
        }
    }

    static public final class TransactionWithFullNameSerde extends WrapperSerde<TransactionWithFullName> {
        public TransactionWithFullNameSerde() {
            super(new JsonSerializer<TransactionWithFullName>(),
                    new JsonDeserializer<TransactionWithFullName>(TransactionWithFullName.class));
        }
    }

}
