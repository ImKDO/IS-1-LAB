package is.lab1.config;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter implements Converter {

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = (ZonedDateTime) objectValue;
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        }
        
        // Handle both String and Timestamp from database
        if (dataValue instanceof String) {
            String dateString = (String) dataValue;
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else if (dataValue instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) dataValue;
            return ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
        }
        
        throw new IllegalArgumentException("Unsupported data type for ZonedDateTime: " + dataValue.getClass());
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
