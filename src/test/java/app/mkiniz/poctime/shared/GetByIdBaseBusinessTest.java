package app.mkiniz.poctime.shared;

import app.mkiniz.poctime.shared.business.base.GetByIdBusinessUseCase;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GetByIdBaseBusinessTest<TKey, TResponse> {

    private Supplier<TKey> keySupplier;
    private GetByIdBusinessUseCase<TKey, TResponse> useCase;
    private BiConsumer<TKey, TResponse> thenConsumer;
    private Function<TKey, TResponse> useCaseFunction;

    public GetByIdBaseBusinessTest<TKey, TResponse> given(Supplier<TKey> keySupplier) {
        this.keySupplier = keySupplier;
        return this;
    }

    public GetByIdBaseBusinessTest<TKey, TResponse> when(Supplier<GetByIdBusinessUseCase<TKey, TResponse>> useCase) {
        this.useCase = useCase.get();
        return this;
    }

    public GetByIdBaseBusinessTest<TKey, TResponse> when(Function<TKey, TResponse> useCaseFunction) {
        this.useCaseFunction = useCaseFunction;
        return this;
    }

    public GetByIdBaseBusinessTest<TKey, TResponse> then(BiConsumer<TKey, TResponse> thenConsumer) {
        this.thenConsumer = thenConsumer;
        return this;
    }

    public void execute() {
        TKey key = keySupplier.get();
        TResponse response = (Objects.nonNull(this.useCase) ? this.useCase.execute(key) : useCaseFunction.apply(key));
        thenConsumer.accept(key, response);
    }

    public static <TKey, TResponse> GetByIdBaseBusinessTest<TKey, TResponse> of() {
        return new GetByIdBaseBusinessTest<TKey, TResponse>();
    }


}
