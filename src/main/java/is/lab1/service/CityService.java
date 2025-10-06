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

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    
    public City saveCity(City city) {
        City savedCity = cityRepository.save(city);
        if (city.getId() == null) {
            webSocketController.notifyCityCreated(savedCity);
        } else {
            webSocketController.notifyCityUpdated(savedCity);
        }
        return savedCity;
    }
    
    public void deleteCity(Integer id) {
        if (!cityRepository.existsById(id)) {
            throw new ResourceNotFoundException("City with id=" + id + " not found");
        }
        cityRepository.deleteById(id);
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

    
    @Transactional
    public void relocatePopulationToMinPopulationCity(Integer fromCityId) {
        cityRepository.relocateToMinFn(fromCityId);
        cityRepository.clearPopulation(fromCityId);
    }

    @Transactional
    public void relocatePopulation(Integer fromCityId, Integer toCityId) {
        if (fromCityId.equals(toCityId)) {
            throw new BadRequestException("Source and target city must be different");
        }
        cityRepository.relocateFn(fromCityId, toCityId);
        cityRepository.clearPopulation(fromCityId);
    }
}
