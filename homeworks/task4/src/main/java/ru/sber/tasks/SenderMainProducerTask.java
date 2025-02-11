package ru.sber.tasks;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.Transaction;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;

import java.time.LocalDateTime;
import java.util.List;

public class SenderMainProducerTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SenderMainProducerTask.class);
    private final ProducerService producerService;
    private final StorageService storageService;

    public SenderMainProducerTask(
            ProducerService producerService, StorageService storageService) {
        this.producerService = producerService;
        this.storageService = storageService;
    }

    @Override
    public void run() {

        log.info("SenderMainProducerTask start: " + LocalDateTime.now()
                + " Thread: " + Thread.currentThread().getName());
        List<Transaction> transactionList = storageService.senderGetStartSendValueList();
        if (transactionList.size() > 0) {
            for (Transaction transaction : transactionList)
                producerService.send(transaction);
            storageService.clearStartSend();
        }
    }
}
