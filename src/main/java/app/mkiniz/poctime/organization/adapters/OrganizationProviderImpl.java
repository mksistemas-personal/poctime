package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.CanRemovePersonUseCase;
import app.mkiniz.poctime.organization.GetOrganizationsNoFoundInListUseCase;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
@AllArgsConstructor
class OrganizationProviderImpl implements OrganizationProvider {

    private final CanRemovePersonUseCase canRemovePersonUseCase;
    private final GetOrganizationsNoFoundInListUseCase getOrganizationsNoFoundInListUseCase;
    private final OrganizationRepository organizationRepository;

    @Override
    public boolean canRemovePerson(Tsid personId) {
        return canRemovePersonUseCase.canRemovePerson(personId);
    }

    @Override
    public List<String> getOrganizationsNotFound(List<String> ids) {
        return getOrganizationsNoFoundInListUseCase.execute(ids);
    }

    @Override
    public Long count() {
        return organizationRepository.count();
    }
}
