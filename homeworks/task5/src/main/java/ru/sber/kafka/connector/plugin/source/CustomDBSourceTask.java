package ru.sber.kafka.connector.plugin.source;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.data.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomDBSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(CustomDBSourceTask.class);
    public static final String DATABASE_NAME_FIELD = "dbname";
    public static final String POSITION_FIELD = "position";
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private String databaseName;
    private String topic;
    private Long dbOffset;

    @Override
    public String version() {
        return new CustomDBSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> map) {
        AbstractConfig config = new AbstractConfig(CustomDBSourceConnector.CONFIG_DEF, map);
        databaseName = config.getString(CustomDBSourceConnector.DB_CONFIG);
        topic = config.getString(CustomDBSourceConnector.TOPIC_CONFIG);
    }

    @Override
    public List<SourceRecord> poll() {
        Map<String, Object> offset = context.offsetStorageReader()
                .offset(Collections.singletonMap(DATABASE_NAME_FIELD, databaseName));
        if (offset != null) {
            Object lastRecordedOffset = offset.get(POSITION_FIELD);
            if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                throw new ConnectException("Offset position is the incorrect type");
            if (lastRecordedOffset != null) {
                log.debug("Skipped to offset {}", lastRecordedOffset);
            }
            dbOffset = (lastRecordedOffset != null) ? (Long) lastRecordedOffset : 0L;
        } else {
            dbOffset = 0L;
        }

        return getAllTransactions(dbOffset)
                .stream()
                .map(result -> new SourceRecord(Collections.singletonMap(DATABASE_NAME_FIELD, databaseName),
                        Collections.singletonMap(POSITION_FIELD, dbOffset), topic, null,
                        null, null, VALUE_SCHEMA, result, System.currentTimeMillis())).toList();
    }

    private List<String> getAllTransactions(long skip) {
        List<String> result = new ArrayList<>();
        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:h2:~/transactions");
                 PreparedStatement stat = conn.prepareStatement("select * from transactionKafka offset ?")) {

                stat.setLong(1, skip);
                try(ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString("transactionJson"));
                    }
                }
            } catch (Exception e) {
                log.error("Ошибка работы с базой данных");
                throw new RuntimeException(e);
            }
        }catch (ClassNotFoundException exception) {
            log.error("Невозможно загрузить класс org.h2.Driver");
            throw new RuntimeException(exception);
        }
        return result;
    }

    @Override
    public void stop() {
    }
}
