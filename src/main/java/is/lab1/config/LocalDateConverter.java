package is.lab1.config;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements Converter {

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        }
        LocalDate localDate = (LocalDate) objectValue;
        return localDate.toString();
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        }
        
        // Handle both String and java.sql.Date from database
        if (dataValue instanceof String) {
            String dateString = (String) dataValue;
            // Handle format like "2025-11-28 +00" - extract just the date part
            if (dateString.contains(" ")) {
                dateString = dateString.split(" ")[0];
            }
            return LocalDate.parse(dateString);
        } else if (dataValue instanceof Date) {
            Date sqlDate = (Date) dataValue;
            return sqlDate.toLocalDate();
        }
        
        throw new IllegalArgumentException("Unsupported data type for LocalDate: " + dataValue.getClass());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        // No initialization needed
    }
}
