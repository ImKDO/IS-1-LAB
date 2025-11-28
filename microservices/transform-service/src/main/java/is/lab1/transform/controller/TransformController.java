package is.lab1.transform.controller;

import is.lab1.transform.dto.CityValidationRequest;
import is.lab1.transform.dto.TransformResponse;
import is.lab1.transform.event.TransformResultMessage;
import is.lab1.transform.service.KafkaProducerService;
import is.lab1.transform.service.TransformService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/validate")
    public ResponseEntity<TransformResponse> transformCities(
            @RequestBody CityValidationRequest request) {
        TransformResponse response = transformService.transformCities(request.getCities());

        String correlationId = UUID.randomUUID().toString();

        // Only publish to Kafka if there are NO errors (all-or-nothing)
        if (response.getErrors().isEmpty() && !response.getValidCities().isEmpty()) {
            TransformResultMessage message = new TransformResultMessage(
                correlationId,
                response.getValidCities(),
                response.getErrors(),
                response.getStats(),
                Instant.now()
            );

            kafkaProducerService.publishTransformResult(message);

            log.info("Transform request processed and published: correlationId={}, records={}, valid={}",
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
