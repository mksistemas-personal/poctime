package app.mkiniz.poctime.organization.domain;

import org.springframework.lang.Nullable;

public record OrganizationSearchRequest(
        @Nullable String name,
        @Nullable String respName,
        @Nullable String responsibleEmail,
        @Nullable String street,
        @Nullable String city,
        @Nullable String stateCode
) {
}
