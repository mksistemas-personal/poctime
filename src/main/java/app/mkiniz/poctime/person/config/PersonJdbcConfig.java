package app.mkiniz.poctime.person.config;

import app.mkiniz.poctime.base.document.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Configuration
public class PersonJdbcConfig extends AbstractJdbcConfiguration {

    private final ObjectMapper objectMapper;

    public PersonJdbcConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected List<?> userConverters() {
        return List.of(new DocumentReadingConverter(objectMapper), new DocumentWritingConverter(objectMapper));
    }

    @ReadingConverter
    public static class DocumentReadingConverter implements Converter<PGobject, Document<?, ?>> {
        private final ObjectMapper objectMapper;

        public DocumentReadingConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Document<?, ?> convert(PGobject source) {
            if (source == null || source.getValue() == null) return null;
            try {
                return objectMapper.readValue(source.getValue(), Document.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @WritingConverter
    public static class DocumentWritingConverter implements Converter<Document<?, ?>, PGobject> {
        private final ObjectMapper objectMapper;

        public DocumentWritingConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public PGobject convert(Document<?, ?> source) {
            if (source == null) return null;
            try {
                PGobject pgObject = new PGobject();
                pgObject.setType("jsonb");
                pgObject.setValue(objectMapper.writeValueAsString(source));
                return pgObject;
            } catch (JsonProcessingException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
