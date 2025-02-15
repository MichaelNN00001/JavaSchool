package ru.sber.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SenderStorageImpl implements SenderStorage {

    private static final Logger log = LoggerFactory.getLogger(SenderStorageImpl.class);

    private final Map<Long, Transaction> unchecked = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> checked = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> forSendAgain = new ConcurrentHashMap();


    @Override
    public void saveUnchecked(long millisecondsKey, Transaction transaction) {
        unchecked.putIfAbsent(millisecondsKey, transaction);
    }

    @Override
    public void saveChecked(long millisecondsKey, Transaction transaction) {
        checked.putIfAbsent(millisecondsKey, transaction);
    }

    @Override
    public void removeUnchecked(long millisecondsKey) {
        unchecked.remove(millisecondsKey);
    }

    @Override
    public List<Long> getUncheckedKeySet() {
        return new ArrayList<>(unchecked.keySet());
    }

    @Override
    public Transaction getUncheckedTransaction(long key) {
        return unchecked.get(key);
    }

    @Override
    public void saveForSendAgain(long millisecondsKey,Transaction transaction) {
        forSendAgain.putIfAbsent(millisecondsKey,transaction);
    }

    @Override
    public boolean isForSendAgainEmpty() {
        return forSendAgain.isEmpty();
    }

    @Override
    public int sizeOfForSendEmpty() {
        return forSendAgain.size();
    }

    @Override
    public List<Transaction> getForSendAgainValueList() {
        return forSendAgain.values().stream().toList();
    }

    @Override
    public boolean isUncheckedEmpty() {
        return unchecked.isEmpty();
    }
}
