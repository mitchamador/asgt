package gbas.gtbch.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Trim string when use @Convert(converter = StringTrimConverter.class)
 */
@Converter
public class StringTrimConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData != null ? dbData.trim() : null;
    }

}