package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
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
class GetEconomicGroupByIdService implements GetByIdBusinessUseCase<Tsid, EconomicGroupResponse> {

    private final EconomicGroupRepository repository;

    @Override
    public EconomicGroupResponse execute(Tsid id) {
        return (EconomicGroupResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findEconomicGroup)
                .map(EconomicGroupResponse::from)
                .fold(this::throwBusinessException, group -> group);
    }

    private Either<? extends BusinessException, EconomicGroup> findEconomicGroup(Tsid id) {
        return repository.findById(id.toLong())
                .map(Either::<BusinessException, EconomicGroup>right)
                .orElseGet(() -> Either.left(new BusinessException(EconomicGroupConstants.ECONOMIC_GROUP_NOT_FOUND)));
    }
}
