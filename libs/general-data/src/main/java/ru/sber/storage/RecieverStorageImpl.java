package ru.sber.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecieverStorageImpl implements RecieverStorage {

    private static final Logger log = LoggerFactory.getLogger(RecieverStorageImpl.class);

    private final Map<Long, Transaction> unchecked = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> checked = new ConcurrentHashMap<>();

    @Override
    public void saveUnchecked(long millisecondsKey, Transaction transaction) {
        unchecked.putIfAbsent(millisecondsKey, transaction);
    }
}
