package app.mkiniz.poctime.shared.business;

import lombok.Builder;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface AddBusinessUseCase<TRequest, TResponse> extends BusinessUseCase<TRequest, TResponse> {
    TResponse execute(TRequest request);

    @Builder
    record AddBusinessDefaultProcess<TRequest, TResponse, TEntity extends EntityCreated>(
            Supplier<Optional<?>> duplicateSupplier,
            Supplier<TEntity> newEntitySupplier,
            String duplicatedMessage,
            Function<TEntity, TEntity> entitySaver,
            Function<TEntity, TResponse> responseSupplier,
            BiConsumer<TEntity, TRequest> businessEntityConsumer) {

        public AddBusinessDefaultProcess {
            Objects.requireNonNull(duplicateSupplier);
            Objects.requireNonNull(newEntitySupplier);
            Objects.requireNonNull(entitySaver);
            Objects.requireNonNull(responseSupplier);
            Objects.requireNonNull(duplicatedMessage);
        }

        public TResponse execute(TRequest request) {
            if (duplicateSupplier.get().isPresent())
                throw new BusinessException(duplicatedMessage);
            TEntity newEntity = newEntitySupplier.get();
            if (Objects.nonNull(businessEntityConsumer))
                businessEntityConsumer.accept(newEntity, request);
            newEntity.created();
            TEntity newEntityCreated = entitySaver.apply(newEntity);

            return responseSupplier.apply(newEntityCreated);
        }
    }


}
