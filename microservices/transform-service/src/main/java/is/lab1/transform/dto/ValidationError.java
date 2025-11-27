package is.lab1.transform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {
    private int rowIndex;
    private String field;
    private String message;
    private String severity; // ERROR, WARNING
}
