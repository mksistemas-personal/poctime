package app.mkiniz.poctime.shared.business;

public interface UpdateBusinessUseCase<TKey, TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(TKey id, final TRequest request);
}
