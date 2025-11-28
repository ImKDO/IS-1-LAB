package is.lab1.importer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "humans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Human {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @DecimalMin(value = "0", inclusive = false, message = "Age must be greater than 0")
    @Column(name = "age")
    private Long age;
    
    @DecimalMin(value = "0", inclusive = false, message = "Height must be greater than 0")
    @Column(name = "height")
    private Double height;
    
    public Human(Long age) {
        this.age = age;
    }
}
