package app.mkiniz.poctime.base.zipcode;

import app.mkiniz.poctime.base.zipcode.brasil.CepResponse;

import java.util.Optional;

public interface ZipCodeUseCase {
    Optional<CepResponse> findByCep(String cep);
}
