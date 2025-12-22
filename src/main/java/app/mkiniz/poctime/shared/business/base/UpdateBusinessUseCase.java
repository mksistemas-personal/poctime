package app.mkiniz.poctime.shared.business.base;

public interface UpdateBusinessUseCase<TKey, TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(TKey id, TRequest request);
}
