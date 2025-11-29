package is.lab1.transform.controller;

import is.lab1.transform.dto.CityValidationRequest;
import is.lab1.transform.dto.TransformResponse;
import is.lab1.transform.event.TransformResultMessage;
import is.lab1.transform.service.TransformService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/transform")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TransformController {

    private final TransformService transformService;
    private final KafkaTemplate<String, TransformResultMessage> kafkaTemplate;

    @Value("${transform.kafka.topic}")
    private String kafkaTopic;

    @PostMapping("/validate")
    public ResponseEntity<TransformResponse> transformCities(
            @RequestBody CityValidationRequest request) {
        TransformResponse response = transformService.transformCities(request.getCities());

        String correlationId = UUID.randomUUID().toString();

        if (response.getErrors().isEmpty() && !response.getValidCities().isEmpty()) {
            TransformResultMessage.TransformStats messageStats = new TransformResultMessage.TransformStats(
                response.getStats().getTotalRecords(),
                response.getStats().getValidRecords(),
                response.getStats().getInvalidRecords(),
                response.getStats().getDuplicatesInFile()
            );

            TransformResultMessage message = new TransformResultMessage(
                correlationId,
                response.getValidCities(),
                response.getErrors(),
                messageStats,
                Instant.now()
            );

            kafkaTemplate.send(kafkaTopic, correlationId, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Transform result published to Kafka: topic={}, correlationId={}, partition={}, offset={}",
                            kafkaTopic, correlationId,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish transform result to Kafka: correlationId={}", correlationId, ex);
                    }
                });

            log.info("Transform request processed: correlationId={}, records={}, valid={}",
                correlationId,
                response.getStats().getTotalRecords(),
                response.getStats().getValidRecords());
        } else {
            log.warn("Transform request has errors - NOT publishing to Kafka: correlationId={}, errors={}",
                correlationId,
                response.getErrors().size());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Transform Service is running");
    }
}
