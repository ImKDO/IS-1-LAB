package is.lab1.queue.service;

import is.lab1.queue.model.ImportQueue;
import is.lab1.queue.model.QueueItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QueueService {

    private final Map<String, ImportQueue> queues = new ConcurrentHashMap<>();

    public ImportQueue createQueue(List<Object> validatedCities) {
        List<QueueItem> items = new ArrayList<>();
        
        for (int i = 0; i < validatedCities.size(); i++) {
            items.add(new QueueItem(i, validatedCities.get(i)));
        }

        ImportQueue queue = new ImportQueue(items);
        queues.put(queue.getQueueId(), queue);
        
        return queue;
    }

    public ImportQueue getQueue(String queueId) {
        return queues.get(queueId);
    }

    public List<ImportQueue> getAllQueues() {
        return new ArrayList<>(queues.values());
    }

    public void updateItemStatus(String queueId, int itemIndex, 
                                 QueueItem.ItemStatus status, String error) {
        ImportQueue queue = queues.get(queueId);
        if (queue != null && itemIndex < queue.getItems().size()) {
            QueueItem item = queue.getItems().get(itemIndex);
            item.setStatus(status);
            item.setError(error);
            
            queue.setProcessedItems((int) queue.getItems().stream()
                .filter(i -> i.getStatus() != QueueItem.ItemStatus.PENDING)
                .count());
            
            queue.setUpdatedAt(LocalDateTime.now());
            
            // Update queue status
            if (queue.getProcessedItems() == queue.getTotalItems()) {
                boolean hasErrors = queue.getItems().stream()
                    .anyMatch(i -> i.getStatus() == QueueItem.ItemStatus.ERROR);
                queue.setStatus(hasErrors ? 
                    ImportQueue.QueueStatus.COMPLETED : 
                    ImportQueue.QueueStatus.COMPLETED);
            }
        }
    }

    public void updateQueueStatus(String queueId, ImportQueue.QueueStatus status) {
        ImportQueue queue = queues.get(queueId);
        if (queue != null) {
            queue.setStatus(status);
            queue.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void deleteQueue(String queueId) {
        queues.remove(queueId);
    }

    public void clearOldQueues() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        queues.entrySet().removeIf(entry -> 
            entry.getValue().getUpdatedAt().isBefore(threshold)
        );
    }
}
