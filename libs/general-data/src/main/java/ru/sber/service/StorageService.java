package ru.sber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.storage.RecieverStorage;
import ru.sber.storage.RecieverStorageImpl;
import ru.sber.storage.SenderStorage;
import ru.sber.storage.SenderStorageImpl;

import java.util.List;
import java.util.stream.Collectors;

public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final RecieverStorage recieverStorage;
    private final SenderStorage senderStorage;


    public StorageService() {
        this.recieverStorage = new RecieverStorageImpl();
        this.senderStorage = new SenderStorageImpl();
    }

    public void senderSaveUncheked(long millisecondsKey, Transaction transaction) {
        senderStorage.saveUnchecked(millisecondsKey, transaction);
    }

    public SenderStorage getSenderStorage() {
        return this.senderStorage;
    }
    public boolean senderIsUnchekedEmpty() {
        return senderStorage.isUncheckedEmpty();
    }

    public void recieverSaveUnchecked(long millisecondsKey, Transaction transaction) {
        recieverStorage.saveUnchecked(millisecondsKey, transaction);
    }

    public void senderSaveChecked(long millisecondsKey, Transaction transaction) {
        senderStorage.saveChecked(millisecondsKey, transaction);
    }

    public void senderRemoveUncheked(long millisecondsKey) {
        senderStorage.removeUnchecked(millisecondsKey);
    }

    public int senderConfirm(ConfirmData confirmData) {

        if (senderStorage.isUncheckedEmpty())
            log.warn("В хранилище нет непроверенных транзакций");
        else {
            List<Long> sortedKeys = senderStorage.getUncheckedKeySet()
                    .stream().sorted().toList();
            Transaction transaction;
            for (long key : sortedKeys)
                if (key <= confirmData.getKey()) {
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
                } else break;
        }
        return senderStorage.sizeOfForSendEmpty();
    }

}
