package app.mkiniz.poctime.shared.business.base;

public interface DeleteBusinessUseCase<TKey, TResponse> extends BusinessUseCase<TKey, TResponse> {
    TResponse execute(TKey id);
}
