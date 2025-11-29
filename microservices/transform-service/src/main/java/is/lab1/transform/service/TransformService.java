package is.lab1.transform.service;

import is.lab1.transform.client.CityServiceClient;
import is.lab1.transform.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransformService {

    private final CityServiceClient cityServiceClient;

    private static final Set<String> VALID_CLIMATES = Set.of(
        "RAIN_FOREST", "MONSOON", "TUNDRA", "DESERT"
    );
    
    private static final Set<String> VALID_GOVERNMENTS = Set.of(
        "ARISTOCRACY", "GERONTOCRACY", "DICTATORSHIP", "KLEPTOCRACY", "PUPPET_STATE"
    );
    
    private static final Set<String> VALID_STANDARDS = Set.of(
        "HIGH", "MEDIUM", "ULTRA_LOW", "NIGHTMARE"
    );

    public TransformResponse transformCities(List<RawCity> rawCities) {
        List<ValidatedCity> validCities = new ArrayList<>();
        List<ValidationError> errors = new ArrayList<>();
        Map<String, Integer> coordinatesMap = new HashMap<>();
        int duplicatesInFile = 0;
        int duplicatesInDatabase = 0;

        // Get existing coordinates from database
        log.info("Fetching existing coordinates from database...");
        Set<String> existingCoordinates = cityServiceClient.getExistingCoordinates();
        log.info("Found {} existing coordinates in database", existingCoordinates.size());

        for (int i = 0; i < rawCities.size(); i++) {
            RawCity raw = rawCities.get(i);
            
            // VALIDATION
            List<ValidationError> rowErrors = validateCity(raw, i);
            
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                continue;
            }

            ValidatedCity validated = normalizeCity(raw);

            String coordKey = String.format("%d,%d",
                validated.getCoordinates().getX(), 
                validated.getCoordinates().getY());
            
            if (coordinatesMap.containsKey(coordKey)) {
                duplicatesInFile++;
                errors.add(new ValidationError(
                    i, 
                    "coordinates", 
                    "Duplicate coordinates with row " + coordinatesMap.get(coordKey) + " in this file",
                    "ERROR"
                ));
                continue;
            }

            if (existingCoordinates.contains(coordKey)) {
                duplicatesInDatabase++;
                errors.add(new ValidationError(
                    i, 
                    "coordinates", 
                    "Coordinates already exist in database",
                    "ERROR"
                ));
                continue;
            }

            coordinatesMap.put(coordKey, i);
            validCities.add(validated);
        }

        TransformResponse.TransformStats stats = new TransformResponse.TransformStats(
            rawCities.size(),
            validCities.size(),
            rawCities.size() - validCities.size(),
            duplicatesInFile + duplicatesInDatabase
        );

        log.info("Transform complete: total={}, valid={}, invalid={}, duplicatesInFile={}, duplicatesInDB={}", 
            stats.getTotalRecords(), stats.getValidRecords(), stats.getInvalidRecords(), 
            duplicatesInFile, duplicatesInDatabase);

        return new TransformResponse(validCities, errors, stats);
    }

    private List<ValidationError> validateCity(RawCity city, int index) {
        List<ValidationError> errors = new ArrayList<>();

        if (city.getName() == null || city.getName().trim().isEmpty()) {
            errors.add(new ValidationError(index, "name", "Name is required", "ERROR"));
        }

        if (city.getCoordinates() == null) {
            errors.add(new ValidationError(index, "coordinates", "Coordinates are required", "ERROR"));
        } else {
            if (city.getCoordinates().getX() == null) {
                errors.add(new ValidationError(index, "coordinates.x", "X coordinate is required", "ERROR"));
            } else {
                try {
                    int x = parseInt(city.getCoordinates().getX());
                    if (x <= -920) {
                        errors.add(new ValidationError(index, "coordinates.x", "X must be > -920", "ERROR"));
                    }
                } catch (Exception e) {
                    errors.add(new ValidationError(index, "coordinates.x", "Invalid number format", "ERROR"));
                }
            }

            if (city.getCoordinates().getY() == null) {
                errors.add(new ValidationError(index, "coordinates.y", "Y coordinate is required", "ERROR"));
            } else {
                try {
                    int y = parseInt(city.getCoordinates().getY());
                    if (y <= -142) {
                        errors.add(new ValidationError(index, "coordinates.y", "Y must be > -142", "ERROR"));
                    }
                } catch (Exception e) {
                    errors.add(new ValidationError(index, "coordinates.y", "Invalid number format", "ERROR"));
                }
            }
        }

        // Area validation
        try {
            float area = parseFloat(city.getArea());
            if (area <= 0) {
                errors.add(new ValidationError(index, "area", "Area must be > 0", "ERROR"));
            }
        } catch (Exception e) {
            errors.add(new ValidationError(index, "area", "Area must be a positive number", "ERROR"));
        }

        // Population validation
        try {
            int population = parseInt(city.getPopulation());
            if (population <= 0) {
                errors.add(new ValidationError(index, "population", "Population must be > 0", "ERROR"));
            }
        } catch (Exception e) {
            errors.add(new ValidationError(index, "population", "Population must be a positive integer", "ERROR"));
        }

        // Climate validation
        if (city.getClimate() == null || city.getClimate().trim().isEmpty()) {
            errors.add(new ValidationError(index, "climate", "Climate is required", "ERROR"));
        } else {
            String climate = normalizeEnum(city.getClimate());
            if (!VALID_CLIMATES.contains(climate)) {
                errors.add(new ValidationError(index, "climate", 
                    "Invalid climate. Valid values: " + VALID_CLIMATES, "ERROR"));
            }
        }

        // StandardOfLiving validation
        if (city.getStandardOfLiving() == null || city.getStandardOfLiving().trim().isEmpty()) {
            errors.add(new ValidationError(index, "standardOfLiving", "StandardOfLiving is required", "ERROR"));
        } else {
            String standard = normalizeEnum(city.getStandardOfLiving());
            if (!VALID_STANDARDS.contains(standard)) {
                errors.add(new ValidationError(index, "standardOfLiving", 
                    "Invalid standardOfLiving. Valid values: " + VALID_STANDARDS, "ERROR"));
            }
        }

        // Government validation
        if (city.getGovernment() != null && !city.getGovernment().trim().isEmpty()) {
            String government = normalizeEnum(city.getGovernment());
            if (!VALID_GOVERNMENTS.contains(government)) {
                errors.add(new ValidationError(index, "government", 
                    "Invalid government. Valid values: " + VALID_GOVERNMENTS, "ERROR"));
            }
        }

        // Governor validation
        if (city.getGovernor() != null) {
            if (city.getGovernor().getAge() != null) {
                try {
                    long age = parseLong(city.getGovernor().getAge());
                    if (age <= 0) {
                        errors.add(new ValidationError(index, "governor.age", "Age must be > 0", "ERROR"));
                    }
                } catch (Exception e) {
                    errors.add(new ValidationError(index, "governor.age", "Invalid number format", "ERROR"));
                }
            }
        }

        return errors;
    }

    private ValidatedCity normalizeCity(RawCity raw) {
        ValidatedCity city = new ValidatedCity();
        
        // Normalize name
        city.setName(raw.getName().trim());
        
        // Normalize coordinates
        ValidatedCity.Coordinates coords = new ValidatedCity.Coordinates();
        coords.setX(parseInt(raw.getCoordinates().getX()));
        coords.setY(parseInt(raw.getCoordinates().getY()));
        city.setCoordinates(coords);
        
        // Normalize numbers
        city.setArea(parseFloat(raw.getArea()));
        city.setPopulation(parseLong(raw.getPopulation()));
        city.setCapital(raw.getCapital() != null ? raw.getCapital() : false);
        
        if (raw.getMetersAboveSeaLevel() != null) {
            city.setMetersAboveSeaLevel(parseLong(raw.getMetersAboveSeaLevel()));
        }
        
        // Normalize enums
        city.setClimate(normalizeEnum(raw.getClimate()));
        city.setStandardOfLiving(normalizeEnum(raw.getStandardOfLiving()));
        
        if (raw.getGovernment() != null && !raw.getGovernment().trim().isEmpty()) {
            city.setGovernment(normalizeEnum(raw.getGovernment()));
        }
        
        // Normalize governor
        if (raw.getGovernor() != null) {
            ValidatedCity.Governor governor = new ValidatedCity.Governor();
            if (raw.getGovernor().getAge() != null) {
                governor.setAge(parseLong(raw.getGovernor().getAge()));
            }
            city.setGovernor(governor);
        }
        
        // Establishment date
        city.setEstablishmentDate(raw.getEstablishmentDate());
        
        return city;
    }

    private String normalizeEnum(String value) {
        return value.trim().toUpperCase().replace(" ", "_");
    }

    private double parseDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private float parseFloat(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return Float.parseFloat(value.toString());
    }

    private int parseInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private long parseLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }
}
