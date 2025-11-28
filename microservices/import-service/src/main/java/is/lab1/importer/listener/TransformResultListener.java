package is.lab1.importer.listener;

import is.lab1.importer.dto.TransformResultMessage;
import is.lab1.importer.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransformResultListener {

    private final ImportService importService;

    @KafkaListener(
        topics = "${import.kafka.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransformResult(TransformResultMessage message) {
        log.info("Received transform result: correlationId={}, validCities={}, errors={}",
            message.getCorrelationId(),
            message.getValidCities().size(),
            message.getErrors().size());

        try {
            importService.importCities(message);
            log.info("Successfully imported cities from transform result: correlationId={}",
                message.getCorrelationId());
        } catch (Exception e) {
            log.error("Failed to import cities from transform result: correlationId={}",
                message.getCorrelationId(), e);
            // В production здесь можно отправить в DLQ (Dead Letter Queue)
        }
    }
}
