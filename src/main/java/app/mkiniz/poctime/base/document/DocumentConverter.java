package app.mkiniz.poctime.base.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DocumentConverter implements AttributeConverter<Document<?, ?>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(Document<?, ?> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar Document para JSON", e);
        }
    }

    @Override
    public Document<?, ?> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : objectMapper.readValue(dbData, Document.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar JSON para Document", e);
        }
    }
}
