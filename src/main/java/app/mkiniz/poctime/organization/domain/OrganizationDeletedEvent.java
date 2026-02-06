package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.address.Address;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrganizationDeletedEvent(
        @NotNull Tsid organizationId,
        @NotNull Tsid responsibleId,
        @NotBlank @Email String responsibleEmail,
        @Valid @NotNull Address address) {
}
