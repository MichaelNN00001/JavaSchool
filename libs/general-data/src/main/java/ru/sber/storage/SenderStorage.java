package ru.sber.storage;

import ru.sber.model.Transaction;

import java.util.List;

public interface SenderStorage {

    void saveUnchecked(long secondsKey, Transaction transaction);
    boolean isUncheckedEmpty();
    void saveChecked(long secondsKey, Transaction transaction);
    void removeUnchecked(long secondsKey);
    List<Long> getUncheckedKeySet();
    Transaction getUncheckedTransaction(long key);
    void saveForSendAgain(long secondsKey,Transaction transaction);
    boolean isForSendAgainEmpty();
    int sizeOfForSendEmpty();
    List<Transaction> getForSendAgainValueList();
    void saveStartSend(long secondsKey, Transaction transaction);
    List<Transaction> getStartSendValueList();
    void clearStartSend();
}
