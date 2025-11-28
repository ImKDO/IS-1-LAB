package is.lab1.transform.dto;

import lombok.Data;
import java.util.Date;

@Data
public class RawCity {
    private String name;
    private Coordinates coordinates;
    private Object area;  // Can be String or Number
    private Object population;
    private Boolean capital;
    private Object metersAboveSeaLevel;
    private String climate;
    private String government;
    private String standardOfLiving;
    private Governor governor;
    private String establishmentDate;

    @Data
    public static class Coordinates {
        private Object x;
        private Object y;
    }

    @Data
    public static class Governor {
        private Object age;
    }
}
