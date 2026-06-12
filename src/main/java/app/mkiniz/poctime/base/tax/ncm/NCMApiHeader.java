package app.mkiniz.poctime.base.tax.ncm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record NCMApiHeader(
        @JsonProperty("Data_Ultima_Atualizacao_NCM")
        String ncmLastUpdate,
        @JsonProperty("Ato")
        String act,
        @JsonProperty("Nomenclaturas")
        List<NCMItem> items
) {
}
