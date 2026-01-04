package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
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
class GetAllOrganizationService implements GetAllBusinessUseCase<Specification<Organization>, Maybe<Slice<OrganizationResponse>>> {
    private final OrganizationRepository organizationRepository;


    @Override
    public Maybe<Slice<OrganizationResponse>> execute(Pageable pageable, @Nullable Specification<Organization> organizationSpecification) {
        return Maybe.fromEval(later(() -> Objects.nonNull(organizationSpecification) ?
                        organizationRepository.findAll(organizationSpecification, pageable) :
                        organizationRepository.findAll(pageable)))
                .filter(Slice::hasContent)
                .map(orgs ->
                        new SliceImpl<>(orgs.map(OrganizationResponse::from).toList(),
                                pageable,
                                orgs.hasNext()));

    }
}
