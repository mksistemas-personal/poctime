package app.mkiniz.poctime.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends
        JpaRepository<Organization, Long>,
        JpaSpecificationExecutor<Organization>,
        OrganizationProjectionRepository {
    Optional<Organization> findByPersonId(Long personId);

    boolean existsByPersonIdOrResponsiblePersonId(Long personId, Long responsibleId);

    @EntityGraph(attributePaths = {"person", "responsiblePerson"})
    Page<Organization> findAll(@Nullable Specification<Organization> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"person", "responsiblePerson"})
    Page<Organization> findAll(Pageable pageable);

    @Query(value = "SELECT id FROM organization WHERE id NOT IN (:ids) AND deleted = false", nativeQuery = true)
    List<Long> findIdsByNotInList(@Param("ids") List<Long> ids);

}
