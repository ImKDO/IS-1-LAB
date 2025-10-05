package is.lab1.repository;

import is.lab1.model.City;
import is.lab1.model.Climate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    
    // Find cities by name containing substring
    @Query("SELECT c FROM City c WHERE c.name LIKE %:substring%")
    List<City> findByNameContaining(@Param("substring") String substring);
    
    // Find cities by climate
    List<City> findByClimate(Climate climate);
    
    // Find city with minimum population
    @Query("SELECT c FROM City c ORDER BY c.population ASC")
    List<City> findCitiesOrderedByPopulation();
    
    default Optional<City> findCityWithMinPopulation() {
        List<City> cities = findCitiesOrderedByPopulation();
        return cities.isEmpty() ? Optional.empty() : Optional.of(cities.getFirst());
    }
    
    // Calculate average meters above sea level
    @Query("SELECT AVG(c.metersAboveSeaLevel) FROM City c WHERE c.metersAboveSeaLevel IS NOT NULL")
    Double getAverageMetersAboveSeaLevel();

    // DB functions
    @Query(value = "SELECT fn_delete_by_climate(:climate)", nativeQuery = true)
    Integer deleteAnyByClimateFn(@Param("climate") String climate);

    @Query(value = "SELECT fn_avg_masl()", nativeQuery = true)
    Double avgMaslFn();

    @Query(value = "SELECT * FROM fn_find_by_name_like(:substr)", nativeQuery = true)
    List<City> findByNameLikeFn(@Param("substr") String substr);

    @Query(value = "SELECT fn_relocate(:fromId, :toId)", nativeQuery = true)
    void relocateFn(@Param("fromId") Integer fromId, @Param("toId") Integer toId);

    @Query(value = "SELECT fn_relocate_to_min(:fromId)", nativeQuery = true)
    Integer relocateToMinFn(@Param("fromId") Integer fromId);
    
    // Find cities with pagination and filtering
    Page<City> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Find all cities with pagination
    Page<City> findAll(Pageable pageable);
}
