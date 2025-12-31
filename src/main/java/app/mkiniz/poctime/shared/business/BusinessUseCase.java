package app.mkiniz.poctime.shared.business;

import cyclops.control.Either;

public interface BusinessUseCase<TRequest, TResponse> {
    default Either<? extends BusinessException, ? extends TResponse> throwBusinessException(BusinessException error) {
        throw error;
    }
}
