package app.mkiniz.poctime.dashboard;

import lombok.Builder;

public interface DashboardQueryUseCase {

    DashboardResponse execute();

    @Builder
    record DashboardResponse(long totalOrganizations, long totalPersons, long totalEconomicGroups, long totalClients) {
    }
}
