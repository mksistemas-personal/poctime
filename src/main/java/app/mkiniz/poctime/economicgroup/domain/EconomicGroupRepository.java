package app.mkiniz.poctime.economicgroup.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EconomicGroupRepository extends
        JpaRepository<EconomicGroup, Long>,
        JpaSpecificationExecutor<EconomicGroup> {
    boolean existsByName(String name);

    @Query(value = "SELECT * FROM economicgroup WHERE search_vector @@ websearch_to_tsquery('portuguese', :term)", nativeQuery = true)
    List<EconomicGroup> search(@Param("term") String term);
}
