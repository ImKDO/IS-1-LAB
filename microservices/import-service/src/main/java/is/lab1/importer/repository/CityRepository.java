package is.lab1.importer.repository;

import is.lab1.importer.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM City c WHERE c.coordinates.x = :x AND c.coordinates.y = :y")
    boolean existsByCoordinates(@Param("x") Integer x, @Param("y") Integer y);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM City c WHERE c.coordinates.x = :x AND c.coordinates.y = :y AND c.id <> :id")
    boolean existsByCoordinatesExcludingCity(@Param("x") Integer x, @Param("y") Integer y, @Param("id") Integer id);
}
