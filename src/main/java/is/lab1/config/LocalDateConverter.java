package is.lab1.config;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import java.sql.Date;
import java.time.LocalDate;

public class LocalDateConverter implements Converter {

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        }
        LocalDate localDate = (LocalDate) objectValue;
        return Date.valueOf(localDate);
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        }
        Date sqlDate = (Date) dataValue;
        return sqlDate.toLocalDate();
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
