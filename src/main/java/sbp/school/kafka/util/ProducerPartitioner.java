package sbp.school.kafka.util;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.model.Transaction;
import sbp.school.kafka.model.TransactionType;

import java.util.List;
import java.util.Map;

public class ProducerPartitioner implements Partitioner {

    private static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

//    private final static Map<String, Integer> PARTITIONS = Arrays
//            .stream(TransactionType.values())
//            .collect(Collectors.toMap(TransactionType::name, TransactionType::getPartitionNumb));

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

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

        Transaction transaction = (Transaction) value;
        int partitionNumber = transaction.getType().getPartitionNumber();
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
