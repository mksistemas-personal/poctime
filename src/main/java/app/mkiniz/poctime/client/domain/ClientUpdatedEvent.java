package app.mkiniz.poctime.client.domain;

import app.mkiniz.poctime.base.address.Address;
import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

@Builder
public record ClientUpdatedEvent(Tsid clientId, PersonResponse person, String clientEmail, Address address) {
}
