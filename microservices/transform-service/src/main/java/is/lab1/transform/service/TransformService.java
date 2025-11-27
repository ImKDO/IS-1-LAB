package is.lab1.transform.service;

import is.lab1.transform.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransformService {

    private static final Set<String> VALID_CLIMATES = Set.of(
        "RAIN_FOREST", "TROPICAL_SAVANNA", "OCEANIC", "POLAR_ICECAP"
    );
    
    private static final Set<String> VALID_GOVERNMENTS = Set.of(
        "CORPORATOCRACY", "PUPPET_STATE", "MERITOCRACY"
    );
    
    private static final Set<String> VALID_STANDARDS = Set.of(
        "VERY_LOW", "ULTRA_LOW", "NIGHTMARE"
    );

    public TransformResponse transformCities(List<RawCity> rawCities) {
        List<ValidatedCity> validCities = new ArrayList<>();
        List<ValidationError> errors = new ArrayList<>();
        Map<String, Integer> coordinatesMap = new HashMap<>();
        int duplicatesInFile = 0;

        for (int i = 0; i < rawCities.size(); i++) {
            RawCity raw = rawCities.get(i);
            
            // VALIDATION
            List<ValidationError> rowErrors = validateCity(raw, i);
            
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                continue;
            }

            // NORMALIZATION
            ValidatedCity validated = normalizeCity(raw);

            // CLEANING - check duplicates
            String coordKey = String.format("%.3f,%.3f", 
                validated.getCoordinates().getX(), 
                validated.getCoordinates().getY());
            
            if (coordinatesMap.containsKey(coordKey)) {
                duplicatesInFile++;
                errors.add(new ValidationError(
                    i, 
                    "coordinates", 
                    "Duplicate coordinates with row " + coordinatesMap.get(coordKey),
                    "WARNING"
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
            duplicatesInFile
        );

        return new TransformResponse(validCities, errors, stats);
    }

    private List<ValidationError> validateCity(RawCity city, int index) {
        List<ValidationError> errors = new ArrayList<>();

        // Name validation
        if (city.getName() == null || city.getName().trim().isEmpty()) {
            errors.add(new ValidationError(index, "name", "Name is required", "ERROR"));
        }

        // Coordinates validation
        if (city.getCoordinates() == null) {
            errors.add(new ValidationError(index, "coordinates", "Coordinates are required", "ERROR"));
        } else {
            if (city.getCoordinates().getX() == null) {
                errors.add(new ValidationError(index, "coordinates.x", "X coordinate is required", "ERROR"));
            } else {
                try {
                    double x = parseDouble(city.getCoordinates().getX());
                    if (x <= -687) {
                        errors.add(new ValidationError(index, "coordinates.x", "X must be > -687", "ERROR"));
                    }
                } catch (Exception e) {
                    errors.add(new ValidationError(index, "coordinates.x", "Invalid number format", "ERROR"));
                }
            }

            if (city.getCoordinates().getY() == null) {
                errors.add(new ValidationError(index, "coordinates.y", "Y coordinate is required", "ERROR"));
            } else {
                try {
                    float y = parseFloat(city.getCoordinates().getY());
                    if (y <= -449) {
                        errors.add(new ValidationError(index, "coordinates.y", "Y must be > -449", "ERROR"));
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

        // Government validation (optional)
        if (city.getGovernment() != null && !city.getGovernment().trim().isEmpty()) {
            String government = normalizeEnum(city.getGovernment());
            if (!VALID_GOVERNMENTS.contains(government)) {
                errors.add(new ValidationError(index, "government", 
                    "Invalid government. Valid values: " + VALID_GOVERNMENTS, "ERROR"));
            }
        }

        // Governor validation
        if (city.getGovernor() == null || city.getGovernor().getHeight() == null) {
            errors.add(new ValidationError(index, "governor.height", "Governor height is required", "ERROR"));
        } else {
            try {
                double height = parseDouble(city.getGovernor().getHeight());
                if (height <= 0) {
                    errors.add(new ValidationError(index, "governor.height", "Height must be > 0", "ERROR"));
                }
            } catch (Exception e) {
                errors.add(new ValidationError(index, "governor.height", "Invalid number format", "ERROR"));
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
        coords.setX(parseDouble(raw.getCoordinates().getX()));
        coords.setY(parseFloat(raw.getCoordinates().getY()));
        city.setCoordinates(coords);
        
        // Normalize numbers
        city.setArea(parseFloat(raw.getArea()));
        city.setPopulation(parseInt(raw.getPopulation()));
        city.setCapital(raw.getCapital() != null ? raw.getCapital() : false);
        
        if (raw.getMetersAboveSeaLevel() != null) {
            city.setMetersAboveSeaLevel(parseFloat(raw.getMetersAboveSeaLevel()));
        }
        
        // Normalize enums
        city.setClimate(normalizeEnum(raw.getClimate()));
        city.setStandardOfLiving(normalizeEnum(raw.getStandardOfLiving()));
        
        if (raw.getGovernment() != null && !raw.getGovernment().trim().isEmpty()) {
            city.setGovernment(normalizeEnum(raw.getGovernment()));
        }
        
        // Normalize governor
        ValidatedCity.Governor governor = new ValidatedCity.Governor();
        governor.setHeight(parseDouble(raw.getGovernor().getHeight()));
        city.setGovernor(governor);
        
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
}
