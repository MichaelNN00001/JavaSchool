package ru.sber.storage;

import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;

import java.util.List;

public interface RecieverStorage {

    void saveUnchecked(long secondsKey, Transaction transaction);
    List<Long> getUncheckedKeySet();
    boolean isUncheckedEmpty();
    Transaction getTransactionByKey(Long key);
    void saveChecked(long secondsKey, Transaction transaction);
    boolean removeUnchecked(long secondsKey, Transaction transaction);
    int getCountOfUnchecked();
}
