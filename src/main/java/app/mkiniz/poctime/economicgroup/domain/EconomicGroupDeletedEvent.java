package app.mkiniz.poctime.economicgroup.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record EconomicGroupDeletedEvent(String id, String name, String description, List<String> organizationIds) {
}
