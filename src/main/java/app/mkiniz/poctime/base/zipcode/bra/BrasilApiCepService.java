package app.mkiniz.poctime.base.zipcode.bra;

import app.mkiniz.poctime.base.zipcode.ZipCodeResponse;
import app.mkiniz.poctime.base.zipcode.ZipCodeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service("zipcode-br")
public class BrasilApiCepService implements ZipCodeUseCase {

    private final RestClient restClient;

    public BrasilApiCepService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://brasilapi.com.br/api/cep/v1").build();
    }

    @Override
    public Optional<ZipCodeResponse> findByCep(String cep) {
        try {
            return Optional.ofNullable(restClient.get()
                            .uri("/{cep}", cep)
                            .retrieve()
                            .body(CepResponse.class))
                    .map(CepResponse::toZipCode);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw e;
        }
    }
}
