package is.lab1.importer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coordinates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "X coordinate cannot be null")
    @DecimalMin(value = "-920", inclusive = false, message = "X coordinate must be greater than -920")
    @Column(name = "x_coordinate", nullable = false)
    private Integer x;
    
    @NotNull(message = "Y coordinate cannot be null")
    @DecimalMin(value = "-142", inclusive = false, message = "Y coordinate must be greater than -142")
    @Column(name = "y_coordinate", nullable = false)
    private Integer y;
    
    public Coordinates(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
}
