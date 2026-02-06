package app.mkiniz.poctime.economicgroup.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EconomicGroupRepository extends
        JpaRepository<EconomicGroup, Long>,
        JpaSpecificationExecutor<EconomicGroup> {
    boolean existsByName(String name);

    List<EconomicGroup> findAll(Specification<EconomicGroup> spec);

    Page<EconomicGroup> findAll(Specification<EconomicGroup> spec, Pageable pageable);

    Page<EconomicGroup> findAll(Pageable pageable);
}
