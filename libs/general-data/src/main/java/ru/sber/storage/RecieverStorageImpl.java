package ru.sber.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecieverStorageImpl implements RecieverStorage {

    private static final Logger log = LoggerFactory.getLogger(RecieverStorageImpl.class);

    private final Map<Long, Transaction> unchecked = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> checked = new ConcurrentHashMap<>();
    private final Map<String, String> referenceAccountFullName = new ConcurrentHashMap<>();

    @Override
    public void saveUnchecked(long secondsKey, Transaction transaction) {
        unchecked.putIfAbsent(secondsKey, transaction);
    }
    @Override
    public List<Long> getUncheckedKeySet() {
        return new ArrayList<>(unchecked.keySet());
    }
    @Override
    public boolean isUncheckedEmpty() {
        return unchecked.isEmpty();
    }

    @Override
    public Transaction getTransactionByKey(Long key) {
        return unchecked.get(key);
    }

    @Override
    public void saveChecked(long secondsKey, Transaction transaction) {
        checked.putIfAbsent(secondsKey, transaction);
    }

    @Override
    public boolean removeUnchecked(long secondsKey, Transaction transaction) {
        return unchecked.remove(secondsKey, transaction);
    }

    @Override
    public int getCountOfUnchecked() {
        return unchecked.size();
    }

    @Override
    public String getReferenceAccountFullName(String account) {
        return referenceAccountFullName.get(account);
    }

    @Override
    public void saveReferenceAccountFullName(String account, String fullName) {
        referenceAccountFullName.putIfAbsent(account, fullName);
    }

}
