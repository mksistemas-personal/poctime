package app.mkiniz.poctime.client.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ClientRepository extends ClientProjectionRepository {
    Optional<Client> findById(Long id);

    long count();

    Optional<Client> findByPersonId(Long personId);

    boolean existsByPersonId(Long personId);

    Slice<Client> findBySearchRequest(@Nullable ClientSearchRequest spec, Pageable pageable);

    Slice<Client> findAll(Pageable pageable);

    Client save(Client client);

    void delete(Client client);
}
