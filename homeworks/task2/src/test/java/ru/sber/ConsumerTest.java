package ru.sber;

import org.junit.Test;
import ru.sber.model.Transaction;
import ru.sber.service.ConsumerService;
import ru.sber.service.StorageService;

public class ConsumerTest {

    @Test
     public void testConsumer() {

        StorageService storageService = new StorageService();
        ConsumerService service = new ConsumerService("consumer.properties", storageService);
        service.listen(Transaction.class);
    }
}
