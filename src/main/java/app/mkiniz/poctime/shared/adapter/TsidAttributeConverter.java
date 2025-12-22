package app.mkiniz.poctime.shared.adapter;

import com.github.f4b6a3.tsid.Tsid;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TsidAttributeConverter implements AttributeConverter<Tsid, Long> {

    @Override
    public Long convertToDatabaseColumn(Tsid attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toLong();
    }

    @Override
    public Tsid convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        return Tsid.from(dbData);
    }
}
