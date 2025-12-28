package app.mkiniz.poctime.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.f4b6a3.tsid.Tsid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public Module tsidModule() {
        SimpleModule module = new SimpleModule();
        
        // Como o TSID deve ser lido do JSON (String -> Tsid)
        module.addDeserializer(Tsid.class, new JsonDeserializer<>() {
            @Override
            public Tsid deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (value == null || value.isEmpty()) {
                    return null;
                }
                // Tenta converter de String (formato Base32) ou de número Longo
                try {
                    return Tsid.from(value);
                } catch (IllegalArgumentException e) {
                    return Tsid.from(Long.parseLong(value));
                }
            }
        });

        // Como o TSID deve ser escrito no JSON (Tsid -> String)
        module.addSerializer(Tsid.class, new com.fasterxml.jackson.databind.JsonSerializer<>() {
            @Override
            public void serialize(Tsid value, com.fasterxml.jackson.core.JsonGenerator gen, com.fasterxml.jackson.databind.SerializerProvider serializers) throws IOException {
                gen.writeString(value.toString()); // ou gen.writeNumber(value.toLong()) se preferir números
            }
        });

        return module;
    }
}
