package ru.sber.util;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sber.model.ConfirmData;
import ru.sber.model.Transaction;
import ru.sber.model.TransactionType;


import java.util.List;
import java.util.Map;

public class ProducerPartitioner implements Partitioner {

    private static final Logger log = LoggerFactory.getLogger(ProducerPartitioner.class);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        log.info("ProducerPartitioner.partition key = {}, value = {}", key, value);
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int size = partitions.size();
        if (keyBytes == null || !(key instanceof String)) {
            log.error("Ошибка при валидации ключа: key: {}", key);
            throw new IllegalArgumentException("Недействительный ключ: key:" + key.toString());
        }
        try {
            TransactionType.valueOf(key.toString());
        } catch (Exception ex) {
            log.error("Ключ отсутствует: {}", key);
            throw new IllegalArgumentException("Ключ отсутствует: " + key);
        }

        int partitionNumber;
        try {
            Transaction transaction = (Transaction) value;
            partitionNumber = transaction.getType().getPartitionNumber();
        } catch (ClassCastException e) {
            partitionNumber = RandomValues.getRandomInt(1, size + 1);
        }

        if(partitionNumber > size) {
            return size - 1;
        }
        return partitionNumber-1;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
