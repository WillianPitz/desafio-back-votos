package br.com.willpitz.messaging.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Metadata {

    private String eventType;
    private String provider;
    private String version;
}
