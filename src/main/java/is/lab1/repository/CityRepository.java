package is.lab1.repository;

import is.lab1.model.City;
import is.lab1.model.Climate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    
    @Query("SELECT c FROM City c WHERE c.name LIKE :substring")
    List<City> findByNameContaining(@Param("substring") String substring);
    
    List<City> findByClimate(Climate climate);
    
    @Query("SELECT c FROM City c ORDER BY c.population ASC")
    List<City> findCitiesOrderedByPopulation();
    
    default Optional<City> findCityWithMinPopulation() {
        List<City> cities = findCitiesOrderedByPopulation();
        return cities.isEmpty() ? Optional.empty() : Optional.of(cities.getFirst());
    }
    
    @Query("SELECT AVG(c.metersAboveSeaLevel) FROM City c WHERE c.metersAboveSeaLevel IS NOT NULL")
    Double getAverageMetersAboveSeaLevel();

    @Modifying
    @Query(value = "DELETE FROM cities WHERE id IN (SELECT id FROM cities WHERE climate = ?1)", nativeQuery = true)
    Integer deleteAnyByClimateFn(String climate);

    @Query(value = "SELECT AVG(meters_above_sea_level) FROM cities WHERE meters_above_sea_level IS NOT NULL", nativeQuery = true)
    Double avgMaslFn();

    @Query(value = "SELECT * FROM cities WHERE name LIKE ?1", nativeQuery = true)
    List<City> findByNameLikeFn(String substr);

    @Modifying
    @Query(value = "UPDATE cities SET population = population + (SELECT population FROM cities WHERE id = ?1) WHERE id = ?2", nativeQuery = true)
    void relocateFn(Integer fromId, Integer toId);
    
    @Modifying
    @Query(value = "UPDATE cities SET population = 0 WHERE id = ?1", nativeQuery = true)
    void clearPopulation(Integer fromId);

    @Modifying
    @Query(value = "UPDATE cities SET population = population + (SELECT population FROM cities WHERE id = ?1) WHERE id = (SELECT id FROM cities ORDER BY population ASC LIMIT 1)", nativeQuery = true)
    Integer relocateToMinFn(Integer fromId);
    
    Page<City> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<City> findAll(Pageable pageable);
    
    // Simple queries without pessimistic locking
    // DB UNIQUE constraint on coordinates ensures uniqueness
    @Query("SELECT c FROM City c WHERE c.coordinates.x = :x AND c.coordinates.y = :y")
    List<City> findByCoordinates(@Param("x") Integer x, @Param("y") Integer y);
    
    @Query("SELECT c FROM City c WHERE c.coordinates.x = :x AND c.coordinates.y = :y AND c.id != :cityId")
    List<City> findByCoordinatesExcludingCity(@Param("x") Integer x, @Param("y") Integer y, @Param("cityId") Integer cityId);
}
