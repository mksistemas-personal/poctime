package app.mkiniz.poctime.economicgroup.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EconomicGroupRepository extends
        JpaRepository<EconomicGroup, Long>,
        JpaSpecificationExecutor<EconomicGroup> {
    boolean existsByName(String name);
}
