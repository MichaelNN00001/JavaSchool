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
    private final Map<Long, Transaction> forSendAgain = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> forStartSend = new ConcurrentHashMap<>();


    @Override
    public void saveUnchecked(long secondsKey, Transaction transaction) {
        unchecked.putIfAbsent(secondsKey, transaction);
    }

    @Override
    public void saveChecked(long secondsKey, Transaction transaction) {
        checked.putIfAbsent(secondsKey, transaction);
    }

    @Override
    public void removeUnchecked(long secondsKey) {
        unchecked.remove(secondsKey);
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
    public void saveForSendAgain(long secondsKey,Transaction transaction) {
        forSendAgain.putIfAbsent(secondsKey,transaction);
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
    public void saveStartSend(long secondsKey, Transaction transaction) {
        forStartSend.putIfAbsent(secondsKey, transaction);
    }

    @Override
    public List<Transaction> getStartSendValueList() {
        return forStartSend.values().stream().toList();
    }

    @Override
    public void clearStartSend() {
        forStartSend.clear();
    }


    @Override
    public boolean isUncheckedEmpty() {
        return unchecked.isEmpty();
    }
}
