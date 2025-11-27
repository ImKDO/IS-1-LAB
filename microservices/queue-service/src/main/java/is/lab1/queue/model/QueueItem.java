package is.lab1.queue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueItem {
    private int index;
    private Object cityData;  // ValidatedCity from Transform service
    private ItemStatus status;
    private String error;

    public QueueItem(int index, Object cityData) {
        this.index = index;
        this.cityData = cityData;
        this.status = ItemStatus.PENDING;
    }

    public enum ItemStatus {
        PENDING, SUCCESS, ERROR
    }
}
