package app.mkiniz.poctime.base.address;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Jacksonized
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
