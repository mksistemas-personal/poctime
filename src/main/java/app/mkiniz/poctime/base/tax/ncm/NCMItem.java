package app.mkiniz.poctime.base.tax.ncm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NCMItem(
        @JsonProperty("Codigo")
        String code,

        @JsonProperty("Descricao")
        String description,

        @JsonProperty("Data_Inicio")
        String startDate,

        @JsonProperty("Data_Fim")
        String endDate,

        @JsonProperty("Tipo_Ato_Ini")
        String initialActType,

        @JsonProperty("Numero_Ato_Ini")
        String initialActNumber,

        @JsonProperty("Ano_Ato_Ini")
        String initialActYear
) {
}
