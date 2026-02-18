package app.mkiniz.poctime.client.adapters;

import app.mkiniz.poctime.client.ClientProvider;
import app.mkiniz.poctime.client.domain.ClientRepository;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
class ClientProviderImpl implements ClientProvider {
    private final ClientRepository repository;

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public boolean canRemovePerson(Tsid personId) {
        return !repository.existsByPersonId(personId.toLong());
    }
}
