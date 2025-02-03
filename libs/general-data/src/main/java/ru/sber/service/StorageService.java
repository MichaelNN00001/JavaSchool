package ru.sber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.storage.RecieverStorage;
import ru.sber.storage.RecieverStorageImpl;
import ru.sber.storage.SenderStorage;
import ru.sber.storage.SenderStorageImpl;
import ru.sber.util.DateTimeToSecond;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final RecieverStorage recieverStorage;
    private final SenderStorage senderStorage;


    public StorageService() {
        this.recieverStorage = new RecieverStorageImpl();
        this.senderStorage = new SenderStorageImpl();
    }

    public List<Transaction> senderGetStartSendValueList() {
        return senderStorage.getStartSendValueList();
    }

    public void senderSaveUncheked(long secondsKey, Transaction transaction) {
        senderStorage.saveUnchecked(secondsKey, transaction);
    }

    public void clearStartSend() {
        senderStorage.clearStartSend();
    }

    public SenderStorage getSenderStorage() {
        return this.senderStorage;
    }

    public boolean senderIsUnchekedEmpty() {
        return senderStorage.isUncheckedEmpty();
    }

    public void recieverSaveUnchecked(long secondsKey, Transaction transaction) {
        recieverStorage.saveUnchecked(secondsKey, transaction);
    }

    public void senderSaveChecked(long secondsKey, Transaction transaction) {
        senderStorage.saveChecked(secondsKey, transaction);
    }

    public void senderSaveStartSend(long secondsKey, Transaction transaction) {
        senderStorage.saveStartSend(secondsKey, transaction);
    }

    public void senderRemoveUncheked(long secondsKey) {
        senderStorage.removeUnchecked(secondsKey);
    }

    public int senderConfirm(ConfirmData confirmData) {

        if (senderStorage.isUncheckedEmpty())
            log.warn("В хранилище senderStorage нет непроверенных транзакций");
        else {
            List<Long> filteredKeys = senderStorage.getUncheckedKeySet()
                    .stream().filter(c -> c < confirmData.getKey()).sorted().toList();
            Transaction transaction;
            for (long key : filteredKeys) {
                transaction = senderStorage.getUncheckedTransaction(key);
                if (transaction != null)
                    if (confirmData.getIds().contains(transaction.getId())) {
                        senderStorage.saveChecked(key, transaction);
                        senderStorage.removeUnchecked(key);
                        log.info("Транзакция с ключём key = {} успешно подтверждена", key);
                    } else {
                        // записать во временную мапу и потом отправить в кафку
                        senderStorage.saveForSendAgain(key, transaction);
                    }
                else log.warn("Не найдена непроверенная транзакция с ключём key = {}", key);
            }
        }
        return senderStorage.sizeOfForSendEmpty();
    }

    public ConfirmData getConfirmDataFromRecieverStorage(Long waitingTimeSec) {

        if (recieverStorage.isUncheckedEmpty())
            log.warn("В хранилище recieverStorage нет непроверенных транзакций");
        else {
            log.info("В хранилище recieverStorage обнаружены транзакции в количестве {} шт.",
                    recieverStorage.getCountOfUnchecked());
            Long limitSeconds = DateTimeToSecond.getDateTimeInSeconds(LocalDateTime.now()) - waitingTimeSec;
            List<Long> filteredKeys = recieverStorage.getUncheckedKeySet()
                    .stream().filter(c -> c < limitSeconds).sorted().toList();
            log.info("Из них удовлетворяют условию по времени создания (-{} секунд назад и раньше) {} шт.",
                    waitingTimeSec, filteredKeys.size());
            Transaction transaction;
            List<String> ids = new ArrayList<>();
            for (long key : filteredKeys) {
                transaction = recieverStorage.getTransactionByKey(key);
                if (transaction != null) {
                    ids.add(transaction.getId());
                    recieverStorage.saveChecked(key, transaction);
                    recieverStorage.removeUnchecked(key, transaction);
                    log.info("В хранилище recieverStorage осталось {} шт.", recieverStorage.getCountOfUnchecked());
                    log.info("Транзакция с ключём key = {} включена в список для подтверждения", key);
                } else
                    log.warn("В хранилище recieverStorage не найдена непроверенная транзакция с ключём key = {}", key);
            }
            if (ids.size() > 0) return new ConfirmData(limitSeconds, ids);
        }
        return null;
    }
}
