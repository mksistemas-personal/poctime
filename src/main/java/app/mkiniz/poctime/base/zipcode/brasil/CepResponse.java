package app.mkiniz.poctime.base.zipcode.brasil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa os dados retornados pela BrasilAPI para consulta de CEP.
 * Exemplo de URL: https://brasilapi.com.br/api/cep/v1/{cep}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CepResponse(
        String cep,
        String state,
        String city,
        String neighborhood,
        String street,
        String service
) {
}
