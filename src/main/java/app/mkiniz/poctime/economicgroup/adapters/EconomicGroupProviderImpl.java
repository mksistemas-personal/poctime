package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.EconomicGroupProvider;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@AllArgsConstructor
class EconomicGroupProviderImpl implements EconomicGroupProvider {

    private final EconomicGroupRepository repository;

    @Override
    public Long count() {
        return repository.count();
    }
}
