package app.mkiniz.poctime.economicgroup.domain;

import org.springframework.lang.Nullable;

public record EconomicGroupSearchRequest(
        @Nullable String term
) {
}
