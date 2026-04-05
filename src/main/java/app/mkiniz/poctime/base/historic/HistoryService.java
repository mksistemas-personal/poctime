package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;

import java.util.Objects;
import java.util.function.BiFunction;

public interface HistoryService {
    Either<BusinessException, HistoryAdded>
    addHistory(HistoryEntity entity,
               BiFunction<HistoryErrorEnum, HistoryEntity, BusinessException> generateBusinessException);


    static record HistoryAdded(HistoryEntity adjustedEntity, HistoryEntity newEntity) {
        public boolean hasAdjustedEntity() {
            return Objects.nonNull(adjustedEntity);
        }
    }

}
