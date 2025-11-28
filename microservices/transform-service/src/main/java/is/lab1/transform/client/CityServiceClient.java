package is.lab1.transform.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CityServiceClient {

    private final RestTemplate restTemplate;

    @Value("${city-service.base-url:http://city-service:8080}")
    private String cityServiceBaseUrl;

    /**
     * Get all existing coordinates from the database
     * @return Set of coordinate strings in format "x,y"
     */
    public Set<String> getExistingCoordinates() {
        try {
            String url = cityServiceBaseUrl + "/api/cities?size=10000";
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            Set<String> coordinates = new HashSet<>();
            
            if (response != null && response.containsKey("content")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> cities = (List<Map<String, Object>>) response.get("content");
                
                for (Map<String, Object> city : cities) {
                    if (city.containsKey("coordinates")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> coords = (Map<String, Object>) city.get("coordinates");
                        
                        // Handle both x/y and x_coordinate/y_coordinate naming
                        Object xObj = coords.getOrDefault("x", coords.get("xCoordinate"));
                        Object yObj = coords.getOrDefault("y", coords.get("yCoordinate"));
                        
                        if (xObj != null && yObj != null) {
                            double x = ((Number) xObj).doubleValue();
                            float y = ((Number) yObj).floatValue();
                            String coordKey = String.format("%.3f,%.3f", x, y);
                            coordinates.add(coordKey);
                        }
                    }
                }
            }
            
            log.info("Retrieved {} existing coordinates from database", coordinates.size());
            return coordinates;
            
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while fetching coordinates: {}", e.getMessage());
            return new HashSet<>();
        } catch (Exception e) {
            log.error("Error fetching existing coordinates from city-service", e);
            return new HashSet<>();
        }
    }
}
