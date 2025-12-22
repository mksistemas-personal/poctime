package app.mkiniz.poctime.shared.business.base;

public interface GetByIdBusinessUseCase<TKey, TResponse> extends BusinessUseCase<TKey, TResponse> {
    TResponse execute(TKey id);
}
