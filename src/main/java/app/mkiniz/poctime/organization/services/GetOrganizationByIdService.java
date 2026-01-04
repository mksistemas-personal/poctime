package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.GetByIdBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetOrganizationByIdService implements GetByIdBusinessUseCase<Tsid, OrganizationResponse> {

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationResponse execute(Tsid id) {
        return (OrganizationResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findOrganization)
                .fold(this::throwBusinessException, OrganizationResponse::from);
    }

    private Either<? extends BusinessException, ? extends Organization> findOrganization(Tsid organizationId) {
        return organizationRepository.findById(organizationId.toLong())
                .<Either<BusinessException, Organization>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_NOT_FOUND)));
    }
}
