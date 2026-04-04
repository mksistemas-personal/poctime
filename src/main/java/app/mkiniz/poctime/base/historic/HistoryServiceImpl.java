package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.BiFunction;

@Component
class HistoryServiceImpl implements HistoryService {
    @Override
    public Either<BusinessException, HistoryEntity<?>> addHistory(HistoryEntity<?> entity, BiFunction<HistoryErrorEnum, HistoryEntity<?>, BusinessException> generateBusinessException) {
        Objects.requireNonNull(entity, "history.entity.is.null");
        Objects.requireNonNull(generateBusinessException, "history.generate.business.exception.is.null");
        if (entity.validFrom().isEmpty())
            return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_FROM_NULL, entity));
        if (entity.validUntil().isPresent())
            return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_UNTIL_NOT_NULL, entity));
        return Either.right(entity);
    }
}
