package is.lab1.transform.event;

import is.lab1.transform.dto.TransformResponse;
import is.lab1.transform.dto.ValidationError;
import is.lab1.transform.dto.ValidatedCity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformResultMessage {
    private String correlationId;
    private List<ValidatedCity> validCities;
    private List<ValidationError> errors;
    private TransformResponse.TransformStats stats;
    private Instant producedAt;
}
