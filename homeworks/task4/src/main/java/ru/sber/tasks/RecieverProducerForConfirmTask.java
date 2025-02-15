package ru.sber.tasks;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.ConfirmData;
import ru.sber.service.ProducerService;
import ru.sber.service.StorageService;

import java.time.LocalDateTime;
import java.util.Optional;

public class RecieverProducerForConfirmTask implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(RecieverProducerForConfirmTask.class);
    private final ProducerService producerService;
    private final StorageService storageService;
    private final Long waitingTimeSec;

    public RecieverProducerForConfirmTask(
            ProducerService producerService, Long waitingTimeSec, StorageService storageService) {
        this.producerService = producerService;
        this.storageService = storageService;
        this.waitingTimeSec = waitingTimeSec;
    }

    @Override
    public void run() {

        log.info("RecieverProducerForConfirmTask start: " + LocalDateTime.now()
                + " Thread: " + Thread.currentThread().getName());
        Optional<ConfirmData> confirmData = storageService.getConfirmDataFromRecieverStorage(waitingTimeSec);
        if (confirmData.isPresent()) producerService.send(confirmData);
    }
}
