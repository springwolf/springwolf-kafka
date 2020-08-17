package io.github.stavshamir.springwolf.producer;

import io.github.stavshamir.springwolf.configuration.KafkaProtocolConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringwolfKafkaProducer {

    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public SpringwolfKafkaProducer(@Autowired KafkaProtocolConfiguration kafkaProtocolConfiguration) {
        Map<String, Object> config = kafkaProtocolConfiguration.getProducerConfiguration();
        DefaultKafkaProducerFactory<String, Map<String, Object>> factory = new DefaultKafkaProducerFactory<>(config);
        this.kafkaTemplate = new KafkaTemplate<>(factory);
    }

    public void send(String topic, Map<String, Object> payload) {
        kafkaTemplate.send(topic, payload);
    }

}
