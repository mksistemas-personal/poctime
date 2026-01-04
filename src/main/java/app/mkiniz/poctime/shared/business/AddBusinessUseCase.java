package app.mkiniz.poctime.shared.business;

public interface AddBusinessUseCase<TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(TRequest request);
}
