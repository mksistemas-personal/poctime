package app.mkiniz.poctime.client.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends
        JpaRepository<Client, Long>,
        JpaSpecificationExecutor<Client>,
        ClientProjectionRepository {
    Optional<Client> findByPersonId(Long personId);

    boolean existsByPersonId(Long personId);

    @EntityGraph(attributePaths = {"person"})
    Page<Client> findAll(@Nullable Specification<Client> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"person"})
    Page<Client> findAll(Pageable pageable);
}
