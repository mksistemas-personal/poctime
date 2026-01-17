package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.CanRemovePersonUseCase;
import app.mkiniz.poctime.organization.OrganizationProvider;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
class OrganizationProviderImpl implements OrganizationProvider {

    private final CanRemovePersonUseCase canRemovePersonUseCase;

    @Override
    public boolean canRemovePerson(Tsid personId) {
        return canRemovePersonUseCase.canRemovePerson(personId);
    }
}
