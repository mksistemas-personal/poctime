package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class GetAllClientService implements GetAllBusinessUseCase<Specification<Client>, Maybe<Slice<ClientResponse>>> {

    private final ClientRepository clientRepository;

    @Override
    public Maybe<Slice<ClientResponse>> execute(Pageable pageable, @Nullable Specification<Client> clientSpecification) {
        return Maybe.fromEval(later(() -> Objects.nonNull(clientSpecification) ?
                        clientRepository.findAll(clientSpecification, pageable) :
                        clientRepository.findAll(pageable)))
                .filter(Slice::hasContent)
                .map(clients ->
                        new SliceImpl<>(clients.map(ClientResponse::from).toList(),
                                pageable,
                                clients.hasNext()));
    }
}
