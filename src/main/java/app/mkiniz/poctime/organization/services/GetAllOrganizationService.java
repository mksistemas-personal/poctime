package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.organization.domain.OrganizationSearchRequest;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllOrganizationService implements GetAllBusinessUseCase<OrganizationSearchRequest, Maybe<Slice<OrganizationResponse>>> {
    private final OrganizationRepository organizationRepository;

    @Override
    public Maybe<Slice<OrganizationResponse>> execute(Pageable pageable, @Nullable OrganizationSearchRequest request) {
        return Maybe.fromEval(later(() -> organizationRepository.findAll(request, pageable)))
                .filter(Slice::hasContent)
                .map(orgs ->
                        new SliceImpl<>(orgs.map(OrganizationResponse::from).toList(),
                                pageable,
                                orgs.hasNext()));

    }
}
