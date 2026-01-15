package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.address.Address;
import com.github.f4b6a3.tsid.Tsid;

public record OrganizationResponse(Tsid organizationId, PersonResponse organizationPerson,
                                   PersonResponse responsiblePerson, Address address, String responsibleEmail) {

    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                Tsid.from(organization.getId()),
                PersonResponse.from(organization.getPerson()),
                PersonResponse.from(organization.getResponsiblePerson()),
                organization.getAddress(),
                organization.getResponsibleEmail());
    }
}
