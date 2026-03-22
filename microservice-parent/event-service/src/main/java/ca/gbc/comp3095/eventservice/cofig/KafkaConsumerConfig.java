////package ca.gbc.comp3095.eventservice.cofig;
////
////import ca.gbc.comp3095.eventservice.dto.GoalCompletedEvent;
////import org.apache.kafka.clients.consumer.ConsumerConfig;
////import org.apache.kafka.common.serialization.StringDeserializer;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
////import org.springframework.kafka.core.ConsumerFactory;
////import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
////import org.springframework.kafka.listener.ContainerProperties;
////import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
////
////import java.util.HashMap;
////import java.util.Map;
////
////@Configuration
////public class KafkaConsumerConfig {
////
////    @Value("${spring.kafka.bootstrap-servers}")
////    private String bootstrapServers;
////
////    @Value("${spring.kafka.properties.schema.registry.url}")
////    private String schemaRegistryUrl;
////
////    @Bean
////    public ConsumerFactory<String, GoalCompletedEvent> consumerFactory() {
////        Map<String, Object> props = new HashMap<>();
////        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
////        props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-service-group");
////        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
////        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
////        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
////                "io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer");
////        props.put("schema.registry.url", schemaRegistryUrl);
////        props.put("specific.value.type", GoalCompletedEvent.class.getName());
////        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
////
////        return new DefaultKafkaConsumerFactory<>(props);
////    }
////
////    @Bean
////    public ConcurrentKafkaListenerContainerFactory<String, GoalCompletedEvent>
////            kafkaListenerContainerFactory() {
////        ConcurrentKafkaListenerContainerFactory<String, GoalCompletedEvent> factory =
////                new ConcurrentKafkaListenerContainerFactory<>();
////        factory.setConsumerFactory(consumerFactory());
////        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
////        return factory;
////    }
////}
//
//package ca.gbc.comp3095.eventservice.config;
//
//import ca.gbc.comp3095.eventservice.dto.GoalCompletedEvent;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.properties.schema.registry.url}")
//    private String schemaRegistryUrl;
//
//    @Bean
//    public ConsumerFactory<String, GoalCompletedEvent> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//
//        // Basic Kafka consumer properties
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-service-group");
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//
//        // Key deserializer
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//
//        // Value deserializer with error handling
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
//                "io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer");
//
//        // Schema Registry & JSON Schema specific settings
//        props.put("schema.registry.url", schemaRegistryUrl);
//        props.put("specific.value.type", GoalCompletedEvent.class.getName());
//        props.put("use.latest.version", true);  // ensures latest schema version is used
//        props.put("json.value.subject.name.strategy",
//                "io.confluent.kafka.serializers.subject.TopicRecordNameStrategy");
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, GoalCompletedEvent> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, GoalCompletedEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//
//        // Manual acknowledgment for exactly-once processing
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//
//        // Optional: enable batch listening if you want
//        // factory.setBatchListener(true);
//
//        return factory;
//    }
//}
