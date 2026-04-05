package app.mkiniz.poctime.base.historic;

import java.time.LocalDate;
import java.util.List;

public interface HistoryEntity<T> {

    T id();

    LocalDate validFrom();

    LocalDate validUntil();

    void validFrom(LocalDate validFrom);

    void validUntil(LocalDate validUntil);

    List<HistoryEntity<T>> getHistory();
}
