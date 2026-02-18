package app.mkiniz.poctime.client.domain;

import app.mkiniz.poctime.base.address.Address;
import com.github.f4b6a3.tsid.Tsid;

public record ClientResponse(Tsid clientId, PersonResponse clientPerson,
                             Address address, String clientEmail) {

    public static ClientResponse from(Client client) {
        return new ClientResponse(
                Tsid.from(client.getId()),
                PersonResponse.from(client.getPerson()),
                client.getAddress(),
                client.getClientEmail());
    }
}
