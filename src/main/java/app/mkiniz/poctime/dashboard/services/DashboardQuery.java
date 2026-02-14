package app.mkiniz.poctime.dashboard.services;

import app.mkiniz.poctime.dashboard.DashboardQueryUseCase;
import app.mkiniz.poctime.economicgroup.EconomicGroupProvider;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.person.PersonProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@AllArgsConstructor
@Component
class DashboardQuery implements DashboardQueryUseCase {

    private final PersonProvider personProvider;
    private final OrganizationProvider organizationProvider;
    private final EconomicGroupProvider economicGroupProvider;

    @Override
    public DashboardResponse execute() {
        return DashboardResponse.builder()
                .totalPersons(personProvider.count())
                .totalOrganizations(organizationProvider.count())
                .totalEconomicGroups(economicGroupProvider.count())
                .build();
    }
}
