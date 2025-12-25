package app.mkiniz.poctime.shared;

import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DeleteBaseBusinessTest<TKey, TResponse> {

    private Supplier<TKey> keySupplier;
    private DeleteBusinessUseCase<TKey, TResponse> useCase;
    private BiConsumer<TKey, TResponse> thenConsumer;
    private Function<TKey, TResponse> useCaseFunction;

    public DeleteBaseBusinessTest<TKey, TResponse> given(Supplier<TKey> keySupplier) {
        this.keySupplier = keySupplier;
        return this;
    }

    public DeleteBaseBusinessTest<TKey, TResponse> when(Supplier<DeleteBusinessUseCase<TKey, TResponse>> useCase) {
        this.useCase = useCase.get();
        return this;
    }

    public DeleteBaseBusinessTest<TKey, TResponse> when(Function<TKey, TResponse> useCaseFunction) {
        this.useCaseFunction = useCaseFunction;
        return this;
    }

    public DeleteBaseBusinessTest<TKey, TResponse> then(BiConsumer<TKey, TResponse> thenConsumer) {
        this.thenConsumer = thenConsumer;
        return this;
    }

    public void execute() {
        TKey key = keySupplier.get();
        TResponse response = (Objects.nonNull(this.useCase) ? this.useCase.execute(key) : useCaseFunction.apply(key));
        thenConsumer.accept(key, response);
    }

    public static <TKey, TResponse> DeleteBaseBusinessTest<TKey, TResponse> of() {
        return new DeleteBaseBusinessTest<TKey, TResponse>();
    }


}
