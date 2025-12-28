package app.mkiniz.poctime.organization.domain.countries;

import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationCountryValidation;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.springframework.stereotype.Component;

@Component("organization-country-br")
public class BrazilianOrganizationCountryValidation implements OrganizationCountryValidation {
    @Override
    public Either<BusinessException, Organization> validateOrganizationByCountry(Organization organization) {
        if (!organization.isPersonAndResponsibleSameCountry())
            return Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_COUNTRY_RESPONSIBLE_INVALID));
        if (!(organization.getPerson().getDocument() instanceof CnpjDocument))
            return Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_PERSON_COUNTRY_WRONG_TYPE));
        if (!(organization.getResponsiblePerson().getDocument() instanceof CpfDocument))
            return Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_RESPONSIBLE_PERSON_COUNTRY_WRONG_TYPE));
        return Either.right(organization);
    }
}
