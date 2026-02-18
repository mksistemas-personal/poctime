package app.mkiniz.poctime.base.address;

import jakarta.persistence.Embeddable;
import lombok.Builder;

@Builder(toBuilder = true)
@Embeddable
public record Address(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String country,
        String stateCode
) {
    public static AddressBuilder builder() {
        return new AddressBuilder();
    }
}
