package is.lab1.transform.client;

import is.lab1.transform.event.TransformResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueServiceClient {

    private final RestTemplate restTemplate;

    @Value("${queue-service.url:http://queue-service:8082}")
    private String queueServiceBaseUrl;

    public void enqueue(TransformResultMessage payload) {
        String url = queueServiceBaseUrl + "/api/queue/messages";
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, payload, Void.class);
            log.info("Queue service responded with status={} correlationId={}",
                response.getStatusCode(), payload.getCorrelationId());
        } catch (RestClientException ex) {
            log.error("Failed to push payload to queue-service", ex);
            throw new IllegalStateException("Queue service unavailable", ex);
        }
    }
}
