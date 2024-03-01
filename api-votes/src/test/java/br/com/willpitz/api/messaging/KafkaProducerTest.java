package br.com.willpitz.api.messaging;

import br.com.willpitz.messaging.VoteResultProducer;
import br.com.willpitz.messaging.messages.VoteResultMessage;
import br.com.willpitz.messaging.payload.KafkaPayload;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final EasyRandom GENERATOR = new EasyRandom(new EasyRandomParameters().seed(1L));

    @InjectMocks
    private VoteResultProducer producer;

    @Mock
    private KafkaTemplate<String, KafkaPayload<VoteResultMessage>> kafkaTemplate;


    @Test
    void shouldSendMessage() {
        producer.send(GENERATOR.nextObject(VoteResultMessage.class));

        verify(kafkaTemplate).send(any(), any());
    }
}
