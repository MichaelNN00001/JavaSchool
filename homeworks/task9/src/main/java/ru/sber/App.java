package ru.sber;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        System.out.println( "Hello World!" );

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount"); // 1
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // 2
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()); // 3
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder(); // 1
        KStream<String, String> source = builder.stream("testTopic");
        final Pattern pattern = Pattern.compile("\\W+");
        KStream counts = source.flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase()))) // 2
                .map((key, value) -> new KeyValue<Object, Object>(value, value))
                .filter((key, value) -> (!value.equals("the"))) // 3
                .groupByKey() // 4
                .count(Named.as("CountStore"))
                .mapValues(value -> Long.toString(value))
                .toStream(); // 5
        counts.to("testTopic-output"); // 6

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props); // 1
        streams.start(); // 2
        Thread.sleep(5000L);
        //streams.close(); // 3
    }
}
