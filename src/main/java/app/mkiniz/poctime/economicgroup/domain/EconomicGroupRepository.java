package app.mkiniz.poctime.economicgroup.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface EconomicGroupRepository {
    boolean existsByName(String name);

    long count();

    Slice<EconomicGroup> findAll(@Nullable EconomicGroupSearchRequest request, Pageable pageable);

    Slice<EconomicGroup> findAll(Pageable pageable);

    Optional<EconomicGroup> findById(Long id);

    List<EconomicGroup> findAllByOrganizationId(String organizationId);

    EconomicGroup save(EconomicGroup economicGroup);

    void delete(EconomicGroup economicGroup);
}
