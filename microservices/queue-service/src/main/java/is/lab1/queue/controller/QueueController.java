package is.lab1.queue.controller;

import is.lab1.queue.dto.TransformResultMessage;
import is.lab1.queue.service.KafkaQueueService;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueController {

    private final KafkaQueueService kafkaQueueService;

    @PostMapping("/messages")
    public ResponseEntity<EnqueueResponse> enqueue(@RequestBody @Validated TransformResultMessage message) {
        kafkaQueueService.enqueue(message);
        log.info("Accepted payload for queue: correlationId={}, cities={} errors={}",
            message.getCorrelationId(),
            message.getValidCities() != null ? message.getValidCities().size() : 0,
            message.getErrors() != null ? message.getErrors().size() : 0);
        return ResponseEntity.accepted().body(new EnqueueResponse(message.getCorrelationId(), Instant.now()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Queue Service is running");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid enqueue request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error while handling queue request", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Failed to enqueue payload"));
    }

    @Data
    @AllArgsConstructor
    private static class EnqueueResponse {
        private String correlationId;
        private Instant acceptedAt;
    }

    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private String message;
    }
}
