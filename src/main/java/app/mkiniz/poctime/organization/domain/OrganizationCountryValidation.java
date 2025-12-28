package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;

public interface OrganizationCountryValidation {

    String ORGANIZATION_COUNTRY = "organization-country-";

    Either<BusinessException, Organization> validateOrganizationByCountry(Organization organization);

    static String getCountry(String countryCode) {
        return (ORGANIZATION_COUNTRY + countryCode).toLowerCase();
    }
}
