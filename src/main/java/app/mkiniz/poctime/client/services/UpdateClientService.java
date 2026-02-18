package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.address.AddressCountry;
import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.client.domain.UpdateClientRequest;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class UpdateClientService implements UpdateBusinessUseCase<Tsid, UpdateClientRequest, ClientResponse> {

    private final ClientRepository clientRepository;
    private final BeanFactory beanFactory;

    @Override
    public ClientResponse execute(Tsid id, UpdateClientRequest clientRequest) {
        return (ClientResponse) Either.<BusinessException, Context>right(new Context(id, clientRequest))
                .flatMap(this::findClient)
                .flatMap(this::updateAddress)
                .flatMap(this::updateClient)
                .map(context -> ClientResponse.from(context.client))
                .fold(error -> {
                    throw error;
                }, response -> response);
    }

    private Either<BusinessException, Context> updateClient(Context context) {
        context.client.setClientEmail(context.request.clientEmail());
        return context.client.valid()
                .map(client -> {
                    client.updated();
                    clientRepository.save(client);
                    return context;
                });
    }

    private Either<BusinessException, Context> updateAddress(Context context) {
        AddressCountry addressCountry = beanFactory.getBean(
                AddressCountry.getCountry(context.getPersonCountry()), AddressCountry.class);
        return addressCountry.validate(context.request.address())
                .map(address -> {
                    Address canonizedAddress = addressCountry.canonicalize(address);
                    if (!Objects.deepEquals(context.client.getAddress(), canonizedAddress))
                        context.client.setAddress(canonizedAddress);
                    return context;
                });
    }

    private Either<BusinessException, Context> findClient(Context context) {
        Optional<Client> client = clientRepository.findById(context.id.toLong());
        client.ifPresent(value -> context.client = value);
        return client.isEmpty() ?
                Either.left(new BusinessException(ClientConstants.CLIENT_NOT_FOUND)) :
                Either.right(context);
    }

    static class Context {
        public UpdateClientRequest request;
        public Client client;
        public Tsid id;

        public Context(Tsid id, UpdateClientRequest request) {
            this.request = request;
            this.id = id;
        }

        public String getPersonCountry() {
            return client.getPerson().getDocument().getCountry();
        }
    }
}
