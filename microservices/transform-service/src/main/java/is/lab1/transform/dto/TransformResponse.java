package is.lab1.transform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformResponse {
    private List<ValidatedCity> validCities;
    private List<ValidationError> errors;
    private TransformStats stats;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransformStats {
        private int totalRecords;
        private int validRecords;
        private int invalidRecords;
        private int duplicatesInFile;
    }
}
