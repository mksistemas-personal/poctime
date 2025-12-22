package app.mkiniz.poctime.shared.business.base;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Pageable;

public interface GetAllBusinessUseCase<TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(Pageable pageable, @Nullable TRequest request);
}
