package app.mkiniz.poctime.base.tax.ncm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NCMHeader(
        @JsonProperty("lastUpdate")
        LocalDateTime lastUpdate,
        @JsonProperty("version")
        String version) {

}
