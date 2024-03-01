package br.com.willpitz.messaging;

import br.com.willpitz.messaging.payload.KafkaPayload;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class KafkaProducer<T> {

    protected KafkaTemplate<String, KafkaPayload<T>> kafkaTemplate;

    public void send(final T message, String eventType, String provider, String version, String topic) {
        KafkaPayload<T> kafkaPayload = new KafkaPayload<>(message, eventType, provider, version);
        kafkaTemplate.send(topic, kafkaPayload);
    }
}
