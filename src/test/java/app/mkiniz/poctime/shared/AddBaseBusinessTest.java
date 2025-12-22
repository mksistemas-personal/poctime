package app.mkiniz.poctime.shared;

import app.mkiniz.poctime.shared.business.base.AddBusinessUseCase;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AddBaseBusinessTest<TRequest, TResponse> {

    private Supplier<TRequest> requestSupplier;
    private AddBusinessUseCase<TRequest, TResponse> useCase;
    private BiConsumer<TRequest, TResponse> thenConsumer;
    private Function<TRequest, TResponse> useCaseFunction;


    public AddBaseBusinessTest<TRequest, TResponse> given(Supplier<TRequest> requestSupplier) {
        this.requestSupplier = requestSupplier;
        return this;
    }

    public AddBaseBusinessTest<TRequest, TResponse> when(Supplier<AddBusinessUseCase<TRequest, TResponse>> useCase) {
        this.useCase = useCase.get();
        return this;
    }

    public AddBaseBusinessTest<TRequest, TResponse> when(Function<TRequest, TResponse> useCaseFunction) {
        this.useCaseFunction = useCaseFunction;
        return this;
    }

    public AddBaseBusinessTest<TRequest, TResponse> then(BiConsumer<TRequest, TResponse> thenConsumer) {
        this.thenConsumer = thenConsumer;
        return this;
    }

    public void execute() {
        TRequest request = requestSupplier.get();
        TResponse response = (Objects.nonNull(this.useCase) ? this.useCase.execute(request) : useCaseFunction.apply(request));
        thenConsumer.accept(request, response);
    }

    public static <TRequest, TResponse> AddBaseBusinessTest<TRequest, TResponse> of() {
        return new AddBaseBusinessTest<TRequest, TResponse>();
    }


}
