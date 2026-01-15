package app.mkiniz.poctime.base.document;

import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.base.document.bra.CpfDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CpfDocument.class, name = "cpf"),
        @JsonSubTypes.Type(value = CnpjDocument.class, name = "cnpj")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Document<TIdentifier, TComplement> {
    TIdentifier identifier();

    String getType();

    String getCountry();

    TComplement getComplement();

    boolean hasComplement();

    @JsonIgnore
    boolean isValid();
}
