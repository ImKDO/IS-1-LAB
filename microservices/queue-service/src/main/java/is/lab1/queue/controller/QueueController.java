package is.lab1.queue.controller;

import is.lab1.queue.model.ImportQueue;
import is.lab1.queue.model.QueueItem;
import is.lab1.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/create")
    public ResponseEntity<ImportQueue> createQueue(@RequestBody Map<String, List<Object>> request) {
        List<Object> cities = request.get("cities");
        ImportQueue queue = queueService.createQueue(cities);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/{queueId}")
    public ResponseEntity<ImportQueue> getQueue(@PathVariable String queueId) {
        ImportQueue queue = queueService.getQueue(queueId);
        if (queue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ImportQueue>> getAllQueues() {
        return ResponseEntity.ok(queueService.getAllQueues());
    }

    @PutMapping("/{queueId}/item/{itemIndex}")
    public ResponseEntity<Void> updateItemStatus(
            @PathVariable String queueId,
            @PathVariable int itemIndex,
            @RequestBody Map<String, Object> update) {
        
        QueueItem.ItemStatus status = QueueItem.ItemStatus.valueOf(
            update.get("status").toString()
        );
        String error = update.get("error") != null ? update.get("error").toString() : null;
        
        queueService.updateItemStatus(queueId, itemIndex, status, error);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{queueId}/status")
    public ResponseEntity<Void> updateQueueStatus(
            @PathVariable String queueId,
            @RequestBody Map<String, String> request) {
        
        ImportQueue.QueueStatus status = ImportQueue.QueueStatus.valueOf(
            request.get("status")
        );
        queueService.updateQueueStatus(queueId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{queueId}")
    public ResponseEntity<Void> deleteQueue(@PathVariable String queueId) {
        queueService.deleteQueue(queueId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Queue Service is running");
    }
}
