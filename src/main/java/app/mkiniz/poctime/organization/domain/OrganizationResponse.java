package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.address.Address;
import com.github.f4b6a3.tsid.Tsid;

public record OrganizationResponse(Tsid organizationId, Address address, Tsid responsibleId, String responsibleEmail) {

    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                Tsid.from(organization.getId()),
                organization.getAddress(),
                Tsid.from(organization.getResponsiblePerson().getId()),
                organization.getResponsibleEmail());
    }
}
