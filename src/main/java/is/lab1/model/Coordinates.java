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
    @DecimalMin(value = "-687", inclusive = false, message = "X coordinate must be greater than -687")
    @Column(name = "x_coordinate", nullable = false)
    private Double x;
    
    @NotNull(message = "Y coordinate cannot be null")
    @DecimalMin(value = "-449", inclusive = false, message = "Y coordinate must be greater than -449")
    @Column(name = "y_coordinate", nullable = false)
    private Float y;
    
    // Constructors
    public Coordinates() {}
    
    public Coordinates(Double x, Float y) {
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
    
    public Double getX() {
        return x;
    }
    
    public void setX(Double x) {
        this.x = x;
    }
    
    public Float getY() {
        return y;
    }
    
    public void setY(Float y) {
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
