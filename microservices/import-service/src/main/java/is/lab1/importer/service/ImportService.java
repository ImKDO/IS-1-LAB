package is.lab1.importer.service;

import is.lab1.importer.dto.TransformResultMessage;
import is.lab1.importer.model.*;
import is.lab1.importer.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportService {

    private final CityRepository cityRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional(rollbackFor = Exception.class)
    public void importCities(TransformResultMessage message) {
        log.info("Starting import from Kafka: correlationId={}, validCities={}",
            message.getCorrelationId(), message.getValidCities().size());

        if (!message.getErrors().isEmpty()) {
            log.error("Import rejected - validation errors present: correlationId={}, errors={}",
                message.getCorrelationId(), message.getErrors().size());
            throw new IllegalStateException("Cannot import cities with validation errors");
        }

        int successCount = 0;

        for (int i = 0; i < message.getValidCities().size(); i++) {
            TransformResultMessage.ValidatedCity validatedCity = message.getValidCities().get(i);

            log.debug("Importing city {} of {}: {}", i + 1, message.getValidCities().size(), validatedCity.getName());

            if (validatedCity.getCoordinates() != null) {
                boolean coordinatesExist = cityRepository.existsByCoordinates(
                    validatedCity.getCoordinates().getX(),
                    validatedCity.getCoordinates().getY()
                );
                
                if (coordinatesExist) {
                    log.error("Duplicate coordinates detected during import: ({}, {}) - rolling back transaction",
                        validatedCity.getCoordinates().getX(),
                        validatedCity.getCoordinates().getY());
                    throw new IllegalStateException(
                        String.format("Duplicate coordinates: (%d, %d) already exist in database",
                            validatedCity.getCoordinates().getX(),
                            validatedCity.getCoordinates().getY()));
                }
            }

            City city = convertToEntity(validatedCity);
            cityRepository.save(city);

            successCount++;
            log.info("Successfully imported city {} of {}: {}", i + 1, message.getValidCities().size(), validatedCity.getName());
        }

        log.info("Completed import for correlationId={}: success={}", 
            message.getCorrelationId(), successCount);
    }

    private City convertToEntity(TransformResultMessage.ValidatedCity validatedCity) {
        City city = new City();
        city.setName(validatedCity.getName());
        
        // Convert coordinates
        if (validatedCity.getCoordinates() != null) {
            Coordinates coordinates = new Coordinates(
                validatedCity.getCoordinates().getX(),
                validatedCity.getCoordinates().getY()
            );
            city.setCoordinates(coordinates);
        }
        
        city.setArea(validatedCity.getArea());
        city.setPopulation(validatedCity.getPopulation());
        city.setCapital(validatedCity.getCapital() != null ? validatedCity.getCapital() : false);
        city.setMetersAboveSeaLevel(validatedCity.getMetersAboveSeaLevel());
        
        // Convert enums
        if (validatedCity.getClimate() != null) {
            city.setClimate(Climate.valueOf(validatedCity.getClimate()));
        }
        
        if (validatedCity.getGovernment() != null && !validatedCity.getGovernment().isEmpty()) {
            city.setGovernment(Government.valueOf(validatedCity.getGovernment()));
        }
        
        if (validatedCity.getStandardOfLiving() != null) {
            city.setStandardOfLiving(StandardOfLiving.valueOf(validatedCity.getStandardOfLiving()));
        }
        
        // Convert governor
        if (validatedCity.getGovernor() != null && validatedCity.getGovernor().getAge() != null) {
            Human governor = new Human(validatedCity.getGovernor().getAge());
            city.setGovernor(governor);
        }
        
        // Convert establishment date - skip for now if not needed
        
        return city;
    }
}
