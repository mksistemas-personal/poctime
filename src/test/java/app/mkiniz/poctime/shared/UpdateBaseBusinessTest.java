package app.mkiniz.poctime.shared;

import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class UpdateBaseBusinessTest<TKey, TRequest, TResponse> {

    private Supplier<TRequest> requestSupplier;
    private UpdateBusinessUseCase<TKey, TRequest, TResponse> useCase;
    private TriConsumer<TKey, TRequest, TResponse> thenConsumer;
    private BiFunction<TKey, TRequest, TResponse> useCaseFunction;


    public UpdateBaseBusinessTest<TKey, TRequest, TResponse> given(Supplier<TRequest> requestSupplier) {
        this.requestSupplier = requestSupplier;
        return this;
    }

    public UpdateBaseBusinessTest<TKey, TRequest, TResponse> when(Supplier<UpdateBusinessUseCase<TKey, TRequest, TResponse>> useCase) {
        this.useCase = useCase.get();
        return this;
    }

    public UpdateBaseBusinessTest<TKey, TRequest, TResponse> when(BiFunction<TKey, TRequest, TResponse> useCaseFunction) {
        this.useCaseFunction = useCaseFunction;
        return this;
    }

    public UpdateBaseBusinessTest<TKey, TRequest, TResponse> then(TriConsumer<TKey, TRequest, TResponse> thenConsumer) {
        this.thenConsumer = thenConsumer;
        return this;
    }

    public void execute(TKey id) {
        TRequest request = requestSupplier.get();
        TResponse response = (Objects.nonNull(this.useCase) ? this.useCase.execute(id, request) : useCaseFunction.apply(id, request));
        thenConsumer.accept(id, request, response);
    }

    public static <TKey, TRequest, TResponse> UpdateBaseBusinessTest<TKey, TRequest, TResponse> of() {
        return new UpdateBaseBusinessTest<>();
    }


}
