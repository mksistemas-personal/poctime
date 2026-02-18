package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.GetByIdBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class GetClientByIdService implements GetByIdBusinessUseCase<Tsid, ClientResponse> {

    private final ClientRepository clientRepository;

    @Override
    public ClientResponse execute(Tsid id) {
        return (ClientResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findClient)
                .fold(this::throwBusinessException, ClientResponse::from);
    }

    private Either<? extends BusinessException, ? extends Client> findClient(Tsid clientId) {
        return clientRepository.findById(clientId.toLong())
                .<Either<BusinessException, Client>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(ClientConstants.CLIENT_NOT_FOUND)));
    }
}
