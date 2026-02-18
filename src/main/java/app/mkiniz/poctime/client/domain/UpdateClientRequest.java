package app.mkiniz.poctime.client.domain;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.client.ClientConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateClientRequest(
        @NotNull(message = ClientConstants.ADDRESS_NOT_NULL)
        Address address,
        @NotBlank(message = ClientConstants.CLIENT_EMAIL_NOT_BLANK)
        @Email(message = ClientConstants.CLIENT_EMAIL_INVALID)
        String clientEmail) {
}
