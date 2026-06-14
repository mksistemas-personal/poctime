package app.mkiniz.poctime.organization.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends OrganizationProjectionRepository {
    Optional<Organization> findById(Long id);

    long count();

    Optional<Organization> findByPersonId(Long personId);

    boolean existsByPersonIdOrResponsiblePersonId(Long personId, Long responsibleId);

    Slice<Organization> findAll(@Nullable OrganizationSearchRequest spec, Pageable pageable);

    Slice<Organization> findAll(Pageable pageable);

    List<Long> findIdsByNotInList(Long[] ids);

    Organization save(Organization organization);

    void delete(Organization organization);
}
