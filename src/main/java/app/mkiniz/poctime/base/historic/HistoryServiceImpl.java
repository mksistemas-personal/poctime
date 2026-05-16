package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Override
    public Either<BusinessException, HistoryEntity> updateHistory(HistoryEntity entity, LocalDate validFrom, LocalDate validUntil,
                                                                  BiFunction<HistoryErrorEnum, HistoryEntity, BusinessException> generateBusinessException) {
        if (!entity.validFrom().isEqual(validFrom))
            return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_FROM_MUST_BE_EQUAL_TO_VALID_FROM, entity));
        if (!Objects.equals(validUntil, entity.validUntil()))
            return Either.left(generateBusinessException.apply(HistoryErrorEnum.VALID_UNTIL_MUST_BE_EQUAL_TO_VALID_UNTIL, entity));
        return Either.right(entity);
    }

    @Override
    public HistoryEntity adjustFromDeletedHistory(HistoryEntity entityToBeDeleted) {
        List<HistoryEntity> histories = entityToBeDeleted.getHistory().stream()
                .sorted((left, right) -> left.validFrom().compareTo(right.validFrom())).toList();
        HistoryEntity first = histories.getFirst();
        HistoryEntity last = histories.getLast();

        if (isSame(first, entityToBeDeleted)) {
            Optional<HistoryEntity> nextFromFirst = histories.stream().skip(1).findFirst();
            nextFromFirst.ifPresent(historyEntity -> historyEntity.validFrom(entityToBeDeleted.validFrom()));
            return nextFromFirst.orElse(null);
        } else if (isSame(last, entityToBeDeleted)) {
            Optional<HistoryEntity> previousFromLast = histories.stream().skip(histories.size() - 2).findFirst();
            previousFromLast.ifPresent(historyEntity -> historyEntity.validUntil(null));
            return previousFromLast.orElse(null);
        } else {
            Optional<HistoryEntity> previous = histories.stream().skip(histories.indexOf(entityToBeDeleted) - 1).findFirst();
            previous.ifPresent(historyEntity -> historyEntity.validUntil(entityToBeDeleted.validFrom()));
            return previous.orElse(null);
        }
    }

    private boolean isSame(HistoryEntity entity, HistoryEntity entityToBeDeleted) {
        return Objects.equals(entity.validFrom(), entityToBeDeleted.validFrom()) &&
                Objects.equals(entity.validUntil(), entityToBeDeleted.validUntil());
    }
}
