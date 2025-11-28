package is.lab1.config;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;

public class ZonedDateTimeConverter implements Converter {

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = (ZonedDateTime) objectValue;
        return Timestamp.from(zonedDateTime.toInstant());
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        }
        Timestamp timestamp = (Timestamp) dataValue;
        return ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
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
