package app.mkiniz.poctime.shared.business;

public interface QueryBusinessUseCase<TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(TRequest request);
}
