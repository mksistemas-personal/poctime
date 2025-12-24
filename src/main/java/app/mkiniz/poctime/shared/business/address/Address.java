package app.mkiniz.poctime.shared.business.address;

import jakarta.persistence.Embeddable;
import lombok.Builder;

@Builder
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
}
