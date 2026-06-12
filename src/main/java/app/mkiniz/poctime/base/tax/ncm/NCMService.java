package app.mkiniz.poctime.base.tax.ncm;

import java.util.Optional;

public interface NCMService {
    void fillRepository();

    Optional<NCMItem> findByCode(String code);
}
