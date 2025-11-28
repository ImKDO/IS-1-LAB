package is.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "coordinates")
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
    
    // Constructors
    public Coordinates() {}
    
    public Coordinates(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getX() {
        return x;
    }
    
    public void setX(Integer x) {
        this.x = x;
    }
    
    public Integer getY() {
        return y;
    }
    
    public void setY(Integer y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "Coordinates{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
