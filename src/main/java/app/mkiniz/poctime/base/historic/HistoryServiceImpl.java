package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.BiFunction;

@Component
public class HistoryServiceImpl implements HistoryService {
    @Override
    public Either<BusinessException, HistoryAdded> addHistory(HistoryEntity entity, BiFunction<HistoryErrorEnum, HistoryEntity, BusinessException> generateBusinessException) {
        Objects.requireNonNull(entity, "history.entity.is.null");
        Objects.requireNonNull(generateBusinessException, "history.generate.business.exception.is.null");
        if (Objects.isNull(entity.validFrom()))
            return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_FROM_NULL, entity));
        if (Objects.nonNull(entity.validUntil()))
            return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_UNTIL_NOT_NULL, entity));
        HistoryEntity lastEntity = entity.getHistory()
                .stream()
                .sorted((e1, e2) -> e1.validFrom().compareTo(e2.validFrom()))
                .filter(e -> Objects.isNull(e.validUntil())).findFirst().orElse(null);
        if (Objects.nonNull(lastEntity)) {
            if (entity.validFrom().isBefore(lastEntity.validFrom()) || entity.validFrom().isEqual(lastEntity.validFrom()))
                return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_FROM_MUST_BE_GREATER_THAN_LAST_ENTRY, entity));
            lastEntity.validUntil(entity.validFrom().minusDays(1));
            entity.validUntil(null);
            return Either.right(new HistoryAdded(lastEntity, entity));
        }
        return Either.right(new HistoryAdded(null, entity));
    }
}
