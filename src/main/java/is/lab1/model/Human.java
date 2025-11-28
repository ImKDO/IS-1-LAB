package is.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "humans")
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
    
    // Constructors
    public Human() {}
    
    public Human(String name, Long age, Double height) {
        this.name = name;
        this.age = age;
        this.height = height;
    }
    
    public Human(Long age) {
        this.age = age;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getAge() {
        return age;
    }
    
    public void setAge(Long age) {
        this.age = age;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "Human{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", height=" + height +
                '}';
    }
}
