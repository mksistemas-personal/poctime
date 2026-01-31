package app.mkiniz.poctime.economicgroup.domain;

import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import lombok.Builder;

import java.util.List;

@Builder
public record EconomicGroupResponse(String id, String name, String description, List<String> organizationIds) {

    public static EconomicGroupResponse from(EconomicGroup economicGroup) {
        return EconomicGroupResponse.builder()
                .id(TsidGenerator.fromLongToString(economicGroup.getId()))
                .name(economicGroup.getName())
                .description(economicGroup.getDescription())
                .organizationIds(economicGroup.getOrganizationIds().stream().toList())
                .build();
    }

}
