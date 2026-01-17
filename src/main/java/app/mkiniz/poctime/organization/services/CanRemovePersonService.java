package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.CanRemovePersonUseCase;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class CanRemovePersonService implements CanRemovePersonUseCase {

    private final OrganizationRepository organizationRepository;

    @Override
    public boolean canRemovePerson(Tsid personId) {
        return !organizationRepository
                .existsByPersonIdOrResponsiblePersonId(personId.toLong(), personId.toLong());
    }
}
