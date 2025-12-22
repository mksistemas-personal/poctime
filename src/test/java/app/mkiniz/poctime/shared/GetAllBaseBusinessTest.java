package app.mkiniz.poctime.shared;

import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GetAllBaseBusinessTest<TRequest, TResponse> {

    private Supplier<TRequest> requestSupplier;
    private GetAllBusinessUseCase<TRequest, TResponse> useCase;
    private BiConsumer<TRequest, TResponse> thenConsumer;
    private BiFunction<Pageable, TRequest, TResponse> useCaseFunction;


    public GetAllBaseBusinessTest<TRequest, TResponse> given(Supplier<TRequest> requestSupplier) {
        this.requestSupplier = requestSupplier;
        return this;
    }

    public GetAllBaseBusinessTest<TRequest, TResponse> when(Supplier<GetAllBusinessUseCase<TRequest, TResponse>> useCase) {
        this.useCase = useCase.get();
        return this;
    }

    public GetAllBaseBusinessTest<TRequest, TResponse> when(BiFunction<Pageable, TRequest, TResponse> useCaseFunction) {
        this.useCaseFunction = useCaseFunction;
        return this;
    }

    public GetAllBaseBusinessTest<TRequest, TResponse> then(BiConsumer<TRequest, TResponse> thenConsumer) {
        this.thenConsumer = thenConsumer;
        return this;
    }

    public void execute(Pageable pageable) {
        TRequest request = requestSupplier.get();
        TResponse response = (Objects.nonNull(this.useCase) ? this.useCase.execute(pageable, request) : useCaseFunction.apply(pageable, request));
        thenConsumer.accept(request, response);
    }

    public static <TRequest, TResponse> GetAllBaseBusinessTest<TRequest, TResponse> of() {
        return new GetAllBaseBusinessTest<TRequest, TResponse>();
    }


}
