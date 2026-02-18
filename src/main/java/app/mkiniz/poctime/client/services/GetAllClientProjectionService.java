package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.client.domain.ClientProjectionResponse;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllClientProjectionService implements GetAllBusinessUseCase<String, Maybe<Slice<ClientProjectionResponse>>> {
    private final ClientRepository clientRepository;

    @Override
    public Maybe<Slice<ClientProjectionResponse>> execute(Pageable pageable, @Nullable String documentType) {
        return Maybe.fromEval(later(() -> clientRepository.findAllProjections(pageable)));
    }
}
