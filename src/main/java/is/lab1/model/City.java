package is.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.eclipse.persistence.annotations.Converter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cities")
@Converter(name = "zonedDateTimeConverter", converterClass = is.lab1.config.ZonedDateTimeConverter.class)
@Converter(name = "localDateConverter", converterClass = is.lab1.config.LocalDateConverter.class)
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
    @org.eclipse.persistence.annotations.Convert("localDateConverter")
    private LocalDate creationDate;
    
    @NotNull(message = "Area cannot be null")
    @DecimalMin(value = "0", inclusive = false, message = "Area must be greater than 0")
    @Column(name = "area", nullable = false)
    private Float area;
    
    @Min(value = 1, message = "Population must be greater than 0")
    @Column(name = "population", nullable = false)
    private long population;
    
    @Column(name = "establishment_date")
    @org.eclipse.persistence.annotations.Convert("zonedDateTimeConverter")
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
    
    // Constructors
    public City() {
        this.creationDate = LocalDate.now();
    }
    
    public City(String name, Coordinates coordinates, Float area, long population, 
                Climate climate, StandardOfLiving standardOfLiving, Human governor) {
        this();
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.population = population;
        this.climate = climate;
        this.standardOfLiving = standardOfLiving;
        this.governor = governor;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Coordinates getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
    
    public LocalDate getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    
    public Float getArea() {
        return area;
    }
    
    public void setArea(Float area) {
        this.area = area;
    }
    
    public long getPopulation() {
        return population;
    }
    
    public void setPopulation(long population) {
        this.population = population;
    }
    
    public ZonedDateTime getEstablishmentDate() {
        return establishmentDate;
    }
    
    public void setEstablishmentDate(ZonedDateTime establishmentDate) {
        this.establishmentDate = establishmentDate;
    }
    
    public boolean isCapital() {
        return capital;
    }
    
    public void setCapital(boolean capital) {
        this.capital = capital;
    }
    
    public Long getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }
    
    public void setMetersAboveSeaLevel(Long metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }
    
    public Climate getClimate() {
        return climate;
    }
    
    public void setClimate(Climate climate) {
        this.climate = climate;
    }
    
    public Government getGovernment() {
        return government;
    }
    
    public void setGovernment(Government government) {
        this.government = government;
    }
    
    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }
    
    public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }
    
    public Human getGovernor() {
        return governor;
    }
    
    public void setGovernor(Human governor) {
        this.governor = governor;
    }
    
    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", area=" + area +
                ", population=" + population +
                ", establishmentDate=" + establishmentDate +
                ", capital=" + capital +
                ", metersAboveSeaLevel=" + metersAboveSeaLevel +
                ", climate=" + climate +
                ", government=" + government +
                ", standardOfLiving=" + standardOfLiving +
                ", governor=" + governor +
                '}';
    }
}
