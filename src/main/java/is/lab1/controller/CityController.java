package is.lab1.controller;

import is.lab1.model.City;
import is.lab1.model.Climate;
import is.lab1.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<Page<City>> getAllCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<City> cities = cityService.getCitiesByName(name, pageable);
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Integer id) {
        Optional<City> city = cityService.getCityById(id);
        return city.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody @Valid City city) {
        City savedCity = cityService.saveCity(city);
        return ResponseEntity.ok(savedCity);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Integer id, @RequestBody @Valid City city) {
        if (cityService.getCityById(id).isPresent()) {
            city.setId(id);
            City updatedCity = cityService.saveCity(city);
            return ResponseEntity.ok(updatedCity);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<City>> searchCitiesByName(@RequestParam String name) {
        List<City> cities = cityService.findCitiesByNameContaining(name);
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/climate/{climate}")
    public ResponseEntity<List<City>> getCitiesByClimate(@PathVariable Climate climate) {
        List<City> cities = cityService.findCitiesByClimate(climate);
        return ResponseEntity.ok(cities);
    }
    
    @DeleteMapping("/climate/{climate}")
    public ResponseEntity<Void> deleteCityByClimate(@PathVariable Climate climate) {
        cityService.deleteCityByClimate(climate);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/average-sea-level")
    public ResponseEntity<Double> getAverageMetersAboveSeaLevel() {
        Double average = cityService.getAverageMetersAboveSeaLevel();
        return ResponseEntity.ok(average);
    }
    
    @PostMapping("/relocate-population")
    public ResponseEntity<Void> relocatePopulation(
            @RequestParam Integer fromCityId, 
            @RequestParam Integer toCityId) {
        cityService.relocatePopulation(fromCityId, toCityId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/relocate-population-to-min")
    public ResponseEntity<Void> relocatePopulationToMinPopulationCity(
            @RequestParam Integer fromCityId) {
        cityService.relocatePopulationToMinPopulationCity(fromCityId);
        return ResponseEntity.ok().build();
    }
}
