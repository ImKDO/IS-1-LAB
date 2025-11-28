package is.lab1.queue.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformResultMessage {
    private String correlationId;
    private List<ValidatedCity> validCities;
    private List<ValidationError> errors;
    private TransformStats stats;
    private Instant producedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidatedCity {
        private String name;
        private Coordinates coordinates;
        private Float area;
        private Long population;
        private Boolean capital;
        private Long metersAboveSeaLevel;
        private String climate;
        private String government;
        private String standardOfLiving;
        private Governor governor;
        private String establishmentDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private Integer x;
        private Integer y;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Governor {
        private Long age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private int rowIndex;
        private String field;
        private String message;
        private String severity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransformStats {
        private int totalRecords;
        private int validRecords;
        private int invalidRecords;
        private int duplicatesInFile;
    }
}
