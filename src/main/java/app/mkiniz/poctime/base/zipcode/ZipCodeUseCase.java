package app.mkiniz.poctime.base.zipcode;

import java.util.Objects;
import java.util.Optional;

public interface ZipCodeUseCase {
    Optional<ZipCodeResponse> findByCep(String cep);

    public static final String DEFAULT_COUNTRY = "br";

    public static String getBeanFromCountry(String country) {
        Objects.requireNonNull(country);
        return "zipcode-" + country.toLowerCase();
    }
}
