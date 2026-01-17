package app.mkiniz.poctime.organization.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface OrganizationProjectionRepository {
    Slice<OrganizationProjectionResponse> findAllProjections(Pageable pageable);
}
