package is.lab1.service;

import is.lab1.controller.WebSocketController;
import is.lab1.model.City;
import is.lab1.model.Climate;
import is.lab1.repository.CityRepository;
import is.lab1.exception.BadRequestException;
import is.lab1.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    
    private final CityRepository cityRepository;
    private final WebSocketController webSocketController;

    @Autowired
    public CityService(CityRepository cityRepository, WebSocketController webSocketController) {
        this.cityRepository = cityRepository;
        this.webSocketController = webSocketController;
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
    
    public Page<City> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable);
    }
    
    public Page<City> getCitiesByName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return cityRepository.findAll(pageable);
        }
        return cityRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    public Optional<City> getCityById(Integer id) {
        return cityRepository.findById(id);
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public City saveCity(City city) {
        if (city.getCoordinates() != null) {
            List<City> existingCities;
            if (city.getId() == null) {
                existingCities = cityRepository.findByCoordinates(
                    city.getCoordinates().getX(), 
                    city.getCoordinates().getY()
                );
            } else {
                existingCities = cityRepository.findByCoordinatesExcludingCity(
                    city.getCoordinates().getX(), 
                    city.getCoordinates().getY(),
                    city.getId()
                );
            }
            
            if (!existingCities.isEmpty()) {
                City existing = existingCities.get(0);
                throw new BadRequestException(
                    String.format("Coordinates (%d, %d) are already occupied by city '%s' (ID: %d)",
                        city.getCoordinates().getX(),
                        city.getCoordinates().getY(),
                        existing.getName(),
                        existing.getId())
                );
            }
        }
        
        City savedCity = cityRepository.save(city);
        if (city.getId() == null) {
            webSocketController.notifyCityCreated(savedCity);
        } else {
            webSocketController.notifyCityUpdated(savedCity);
        }
        return savedCity;
    }
    
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteCity(Integer id) {
        City city = cityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("City with id=" + id + " not found"));
        
        cityRepository.delete(city);
        webSocketController.notifyCityDeleted(id);
    }
    
    public List<City> findCitiesByNameContaining(String substring) {
        return cityRepository.findByNameLikeFn("%" + substring + "%");
    }
    
    public List<City> findCitiesByClimate(Climate climate) {
        return cityRepository.findByClimate(climate);
    }
    
    public void deleteCityByClimate(Climate climate) {
        Integer deletedId = cityRepository.deleteAnyByClimateFn(climate.name());
        if (deletedId == null || deletedId == 0) {
            throw new ResourceNotFoundException("No cities found for climate " + climate);
        }
    }
    
    public Double getAverageMetersAboveSeaLevel() {
        return cityRepository.avgMaslFn();
    }
    
    public Optional<City> getCityWithMinPopulation() {
        return cityRepository.findCityWithMinPopulation();
    }

    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void relocatePopulationToMinPopulationCity(Integer fromCityId) {
        cityRepository.relocateToMinFn(fromCityId);
        cityRepository.clearPopulation(fromCityId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void relocatePopulation(Integer fromCityId, Integer toCityId) {
        if (fromCityId.equals(toCityId)) {
            throw new BadRequestException("Source and target city must be different");
        }
        cityRepository.relocateFn(fromCityId, toCityId);
        cityRepository.clearPopulation(fromCityId);
    }
}
