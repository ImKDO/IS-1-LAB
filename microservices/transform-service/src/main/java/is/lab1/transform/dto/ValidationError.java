package is.lab1.transform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    private int rowIndex;
    private String field;
    private String message;
    private String severity; // ERROR, WARNING
}
