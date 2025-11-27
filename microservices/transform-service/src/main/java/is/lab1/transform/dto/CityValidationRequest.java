package is.lab1.transform.dto;

import lombok.Data;
import java.util.List;

@Data
public class CityValidationRequest {
    private List<RawCity> cities;
}
