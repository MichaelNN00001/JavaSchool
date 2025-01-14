package ru.sber;

import org.junit.Test;
import ru.sber.service.ConsumerService;

public class ConsumerTest {

    @Test
     public void testConsumer() {

        ConsumerService service = new ConsumerService("consumer.properties");
        service.lesten();
    }
}
