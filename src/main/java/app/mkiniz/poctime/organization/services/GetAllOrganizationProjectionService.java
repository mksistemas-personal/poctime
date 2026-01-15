package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationProjection;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllOrganizationProjectionService implements GetAllBusinessUseCase<Specification<Organization>, Maybe<Slice<OrganizationProjection>>> {
    private final OrganizationRepository organizationRepository;

    @Override
    public Maybe<Slice<OrganizationProjection>> execute(Pageable pageable, @Nullable Specification<Organization> organizationSpecification) {
        return Maybe.fromEval(later(() -> Objects.nonNull(organizationSpecification) ?
                        organizationRepository.findAll(organizationSpecification, pageable) :
                        organizationRepository.findAll(pageable)))
                .filter(Slice::hasContent)
                .map(orgs ->
                        new SliceImpl<>(orgs.map(OrganizationProjection::from).toList(),
                                pageable,
                                orgs.hasNext()));
    }
}
