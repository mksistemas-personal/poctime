package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupSearchRequest;
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
class GetAllEconomicGroupService implements GetAllBusinessUseCase<EconomicGroupSearchRequest, Maybe<Slice<EconomicGroupResponse>>> {

    private final EconomicGroupRepository repository;

    @Override
    public Maybe<Slice<EconomicGroupResponse>> execute(Pageable pageable, @Nullable EconomicGroupSearchRequest request) {
        return Maybe.fromEval(later(() -> repository.findAll(request, pageable)))
                .filter(Slice::hasContent)
                .map(groups ->
                        new SliceImpl<>(groups.map(EconomicGroupResponse::from).toList(),
                                pageable,
                                groups.hasNext()));
    }
}
