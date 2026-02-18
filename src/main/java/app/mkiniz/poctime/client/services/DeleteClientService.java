package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class DeleteClientService implements DeleteBusinessUseCase<Tsid, ClientResponse> {

    private final ClientRepository clientRepository;

    @Override
    public ClientResponse execute(Tsid id) {
        return (ClientResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findClient)
                .flatMap(this::deleteClient)
                .map(ClientResponse::from)
                .fold(error -> {
                    throw error;
                }, response -> response);
    }

    private Either<? extends BusinessException, ? extends Client> deleteClient(Client client) {
        client.deleted();
        clientRepository.delete(client);
        return Either.right(client);
    }

    private Either<? extends BusinessException, ? extends Client> findClient(Tsid clientId) {
        Optional<Client> client = clientRepository.findById(clientId.toLong());
        return client.<Either<? extends BusinessException, ? extends Client>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(ClientConstants.CLIENT_NOT_FOUND)));
    }
}
