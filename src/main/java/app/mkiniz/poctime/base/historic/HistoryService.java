package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiFunction;

public interface HistoryService {
    Either<BusinessException, HistoryAdded>
    addHistory(HistoryEntity entity,
               BiFunction<HistoryErrorEnum, HistoryEntity, BusinessException> generateBusinessException);

    Either<BusinessException, HistoryEntity>
    updateHistory(HistoryEntity entity, LocalDate validFrom, LocalDate validUntil,
                  BiFunction<HistoryErrorEnum, HistoryEntity, BusinessException> generateBusinessException);

    HistoryEntity adjustFromDeletedHistory(HistoryEntity entityBeDeleted);

    static record HistoryAdded(HistoryEntity adjustedEntity, HistoryEntity newEntity) {
        public boolean hasAdjustedEntity() {
            return Objects.nonNull(adjustedEntity);
        }
    }

}
