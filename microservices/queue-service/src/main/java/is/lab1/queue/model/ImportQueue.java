package is.lab1.queue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportQueue {
    private String queueId;
    private List<QueueItem> items;
    private QueueStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalItems;
    private int processedItems;

    public ImportQueue(List<QueueItem> items) {
        this.queueId = UUID.randomUUID().toString();
        this.items = items;
        this.status = QueueStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalItems = items.size();
        this.processedItems = 0;
    }

    public enum QueueStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
