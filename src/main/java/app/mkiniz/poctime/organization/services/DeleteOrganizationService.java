package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class DeleteOrganizationService implements DeleteBusinessUseCase<Tsid, OrganizationResponse> {

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationResponse execute(Tsid id) {
        return (OrganizationResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findOrganization)
                .flatMap(this::deleteOrganization)
                .map(OrganizationResponse::from)
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<? extends BusinessException, ? extends Organization> deleteOrganization(Organization organization) {
        organization.deleted();
        organizationRepository.delete(organization);
        return Either.right(organization);
    }

    private Either<? extends BusinessException, ? extends Organization> findOrganization(Tsid organizationId) {
        Optional<Organization> org = organizationRepository.findById(organizationId.toLong());
        return org.<Either<? extends BusinessException, ? extends Organization>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_NOT_FOUND)));
    }
}
