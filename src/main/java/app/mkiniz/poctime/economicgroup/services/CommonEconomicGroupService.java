package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.shared.business.BusinessException;

import java.util.List;
import java.util.Optional;

public interface CommonEconomicGroupService {

    default Optional<BusinessException> validateOrganizations(OrganizationProvider provider, List<String> organizationIds) {
        List<String> response = provider.getOrganizationsNotFound(organizationIds);
        if (response.isEmpty()) {
            return Optional.empty();
        }
        String responseAgregateList = String.join(",", response);
        String exceptionMessage = String.format(EconomicGroupConstants.ORGANIZATIONS_NOT_FOUND, responseAgregateList);
        return Optional.of(new BusinessException(exceptionMessage));
    }

}
