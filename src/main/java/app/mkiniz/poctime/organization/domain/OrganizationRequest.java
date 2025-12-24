package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.shared.business.address.Address;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationRequest(
        @NotNull(message = OrganizationConstants.PERSON_ID_NOT_NULL)
        Tsid personId,
        @NotNull(message = OrganizationConstants.ADDRESS_NOT_NULL)
        Address address,
        @NotNull(message = OrganizationConstants.RESPONSIBLE_PERSON_ID_NOT_NULL)
        Tsid responsiblePersonId,
        @NotBlank(message = OrganizationConstants.RESPONSIBLE_EMAIL_NOT_BLANK)
        @Email(message = OrganizationConstants.RESPONSIBLE_EMAIL_INVALID)
        String responsibleEmail) {
}
