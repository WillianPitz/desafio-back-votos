package br.com.willpitz.messaging.payload;

import lombok.Data;

@Data
public class KafkaPayload<T> {

    private static final String DEFAULT_VERSION = "1.0";
    private T payload;
    private Metadata metadata = new Metadata();

    public KafkaPayload(T payload, String eventType, String provider, String version) {
        this.payload = payload;
        this.metadata.setEventType(eventType);
        this.metadata.setProvider(provider);
        this.metadata.setVersion(version != null ? version : DEFAULT_VERSION);
    }
}
