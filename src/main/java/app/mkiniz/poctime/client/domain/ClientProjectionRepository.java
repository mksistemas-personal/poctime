package app.mkiniz.poctime.client.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ClientProjectionRepository {
    Slice<ClientProjectionResponse> findAllProjections(Pageable pageable);

}
