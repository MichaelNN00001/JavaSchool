package ru.sber.storage;

import ru.sber.model.Transaction;

public interface RecieverStorage {

    void saveUnchecked(long millisecondsKey, Transaction transaction);

}
