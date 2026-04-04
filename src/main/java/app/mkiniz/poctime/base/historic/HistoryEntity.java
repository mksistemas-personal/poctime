package app.mkiniz.poctime.base.historic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoryEntity<T> {

    T id();

    Optional<LocalDate> validFrom();

    Optional<LocalDate> validUntil();

    void validFrom(LocalDate validFrom);

    void validUntil(LocalDate validUntil);

    List<HistoryEntity<T>> getHistory();
}
