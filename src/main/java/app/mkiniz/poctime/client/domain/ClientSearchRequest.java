package app.mkiniz.poctime.client.domain;

import org.springframework.lang.Nullable;

public record ClientSearchRequest(
        @Nullable String name,
        @Nullable String street,
        @Nullable String city,
        @Nullable String stateCode,
        @Nullable String email
) {
}
