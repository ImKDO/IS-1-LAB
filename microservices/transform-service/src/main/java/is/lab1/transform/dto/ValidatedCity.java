package is.lab1.transform.dto;

import lombok.Data;

@Data
public class ValidatedCity {
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

    @Data
    public static class Coordinates {
        private Integer x;
        private Integer y;
    }

    @Data
    public static class Governor {
        private Long age;
    }
}
