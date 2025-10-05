package is.lab1.controller;

import is.lab1.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private static final String TOPIC_CITIES = "/topic/cities";

    public void notifyCityCreated(City city) {
        messagingTemplate.convertAndSend(TOPIC_CITIES, "CREATED:" + city.getId());
    }
    
    public void notifyCityUpdated(City city) {
        messagingTemplate.convertAndSend(TOPIC_CITIES, "UPDATED:" + city.getId());
    }
    
    public void notifyCityDeleted(Integer cityId) {
        messagingTemplate.convertAndSend(TOPIC_CITIES, "DELETED:" + cityId);
    }
}
