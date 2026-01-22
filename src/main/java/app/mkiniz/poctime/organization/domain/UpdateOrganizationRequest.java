package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.organization.OrganizationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrganizationRequest(
        @NotNull(message = OrganizationConstants.ADDRESS_NOT_NULL)
        Address address,
        @NotNull(message = OrganizationConstants.RESPONSIBLE_PERSON_ID_NOT_NULL)
        PersonRequest responsiblePerson,
        @NotBlank(message = OrganizationConstants.RESPONSIBLE_EMAIL_NOT_BLANK)
        @Email(message = OrganizationConstants.RESPONSIBLE_EMAIL_INVALID)
        String responsibleEmail) {
}
