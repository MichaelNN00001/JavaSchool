package ru.sber.storage;

import ru.sber.model.Transaction;

import java.util.List;

public interface SenderStorage {

    void saveUnchecked(long millisecondsKey, Transaction transaction);
    boolean isUncheckedEmpty();
    void saveChecked(long millisecondsKey, Transaction transaction);
    void removeUnchecked(long millisecondsKey);
    List<Long> getUncheckedKeySet();
    Transaction getUncheckedTransaction(long key);
    void saveForSendAgain(long millisecondsKey,Transaction transaction);
    boolean isForSendAgainEmpty();
    int sizeOfForSendEmpty();
    List<Transaction> getForSendAgainValueList();
}
