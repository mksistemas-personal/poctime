package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;

import java.util.function.BiFunction;

public interface HistoryService {
    Either<BusinessException, ? extends HistoryEntity<?>>
    addHistory(HistoryEntity<?> entity,
               BiFunction<HistoryErrorEnum, HistoryEntity<?>, BusinessException> generateBusinessException);
}
