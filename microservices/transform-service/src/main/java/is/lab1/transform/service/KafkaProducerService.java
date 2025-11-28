package is.lab1.transform.service;

import is.lab1.transform.event.TransformResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, TransformResultMessage> kafkaTemplate;

    @Value("${transform.kafka.topic}")
    private String transformTopic;

    public void publishTransformResult(TransformResultMessage message) {
        try {
            CompletableFuture<SendResult<String, TransformResultMessage>> future =
                kafkaTemplate.send(transformTopic, message.getCorrelationId(), message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish transform result to Kafka", ex);
                } else if (result != null && result.getRecordMetadata() != null) {
                    log.debug("Published transform result to topic {} partition {} offset {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.debug("Published transform result to topic {}", transformTopic);
                }
            });
        } catch (Exception ex) {
            log.error("Unexpected error while publishing transform result to Kafka", ex);
        }
    }
}
