package is.lab1.importer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cities_id_seq")
    @SequenceGenerator(name = "cities_id_seq", sequenceName = "cities_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;
    
    @NotBlank(message = "Name cannot be null or empty")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull(message = "Coordinates cannot be null")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;
    
    @NotNull(message = "Creation date cannot be null")
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;
    
    @NotNull(message = "Area cannot be null")
    @DecimalMin(value = "0", inclusive = false, message = "Area must be greater than 0")
    @Column(name = "area", nullable = false)
    private Float area;
    
    @Min(value = 1, message = "Population must be greater than 0")
    @Column(name = "population", nullable = false)
    private long population;
    
    @Column(name = "establishment_date")
    private ZonedDateTime establishmentDate;
    
    @Column(name = "capital", nullable = false)
    private boolean capital = false;
    
    @Column(name = "meters_above_sea_level")
    private Long metersAboveSeaLevel;
    
    @NotNull(message = "Climate cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "climate", nullable = false)
    private Climate climate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "government")
    private Government government;
    
    @NotNull(message = "Standard of living cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "standard_of_living", nullable = false)
    private StandardOfLiving standardOfLiving;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "governor_id", nullable = true)
    private Human governor;
    
    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDate.now();
        }
    }
}
