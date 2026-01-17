package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationProjectionResponse;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllOrganizationProjectionService implements GetAllBusinessUseCase<Specification<Organization>, Maybe<Slice<OrganizationProjectionResponse>>> {
    private final OrganizationRepository organizationRepository;

    @Override
    public Maybe<Slice<OrganizationProjectionResponse>> execute(Pageable pageable, @Nullable Specification<Organization> organizationSpecification) {
        return Maybe.fromEval(later(() -> organizationRepository.findAllProjections(pageable)));
    }
}
