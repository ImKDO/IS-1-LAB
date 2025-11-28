package is.lab1.queue.service;

import is.lab1.queue.dto.TransformResultMessage;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaQueueService {

    private final KafkaTemplate<String, TransformResultMessage> kafkaTemplate;

    @Value("${queue.kafka.topic}")
    private String queueTopic;

    public void enqueue(TransformResultMessage message) {
        Objects.requireNonNull(message, "TransformResultMessage must not be null");

        if (message.getValidCities() == null || message.getValidCities().isEmpty()) {
            throw new IllegalArgumentException("Cannot enqueue empty payload: validCities is empty");
        }

        if (message.getCorrelationId() == null || message.getCorrelationId().isBlank()) {
            message.setCorrelationId(UUID.randomUUID().toString());
        }

        if (message.getProducedAt() == null) {
            message.setProducedAt(Instant.now());
        }

        kafkaTemplate.send(queueTopic, message.getCorrelationId(), message)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to enqueue message correlationId={} topic={}", message.getCorrelationId(), queueTopic, ex);
                } else {
                    log.info("Message enqueued correlationId={} topic={} partition={} offset={}",
                        message.getCorrelationId(), queueTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}
