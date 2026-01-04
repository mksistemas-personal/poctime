package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.address.Address;
import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

@Builder
public record OrganizationAddedEvent(Tsid organizationId, Tsid responsibleId, String responsibleEmail, Address address) {
}
