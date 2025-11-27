package is.lab1.importer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportService {

    private final WebClient.Builder webClientBuilder;

    @Value("${city.service.url:http://localhost:8080}")
    private String cityServiceUrl;

    @Value("${queue.service.url:http://localhost:8082}")
    private String queueServiceUrl;

    public void importQueue(String queueId) {
        log.info("Starting import for queue: {}", queueId);

        // Get queue from Queue Service
        WebClient queueClient = webClientBuilder.baseUrl(queueServiceUrl).build();
        
        Map<String, Object> queue = queueClient.get()
            .uri("/api/queue/{queueId}", queueId)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        if (queue == null) {
            log.error("Queue not found: {}", queueId);
            return;
        }

        // Update queue status to PROCESSING
        queueClient.put()
            .uri("/api/queue/{queueId}/status", queueId)
            .bodyValue(Map.of("status", "PROCESSING"))
            .retrieve()
            .toBodilessEntity()
            .block();

        List<Map<String, Object>> items = (List<Map<String, Object>>) queue.get("items");
        WebClient cityClient = webClientBuilder.baseUrl(cityServiceUrl).build();

        // Process each item
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            Object cityData = item.get("cityData");

            try {
                log.info("Importing city {} of {}", i + 1, items.size());

                // Send to City Service (main backend)
                cityClient.post()
                    .uri("/api/cities")
                    .bodyValue(cityData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

                // Update item status to SUCCESS
                queueClient.put()
                    .uri("/api/queue/{queueId}/item/{itemIndex}", queueId, i)
                    .bodyValue(Map.of("status", "SUCCESS"))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

                log.info("Successfully imported city {} of {}", i + 1, items.size());

            } catch (Exception e) {
                log.error("Failed to import city {} of {}: {}", i + 1, items.size(), e.getMessage());

                // Update item status to ERROR
                queueClient.put()
                    .uri("/api/queue/{queueId}/item/{itemIndex}", queueId, i)
                    .bodyValue(Map.of(
                        "status", "ERROR",
                        "error", e.getMessage()
                    ))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            }
        }

        // Update queue status to COMPLETED
        queueClient.put()
            .uri("/api/queue/{queueId}/status", queueId)
            .bodyValue(Map.of("status", "COMPLETED"))
            .retrieve()
            .toBodilessEntity()
            .block();

        log.info("Completed import for queue: {}", queueId);
    }
}
