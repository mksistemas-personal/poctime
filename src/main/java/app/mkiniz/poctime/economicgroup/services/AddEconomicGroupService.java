package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
class AddEconomicGroupService implements AddBusinessUseCase<EconomicGroupRequest, EconomicGroupResponse> {

    private final EconomicGroupRepository economicGroupRepository;
    private final OrganizationProvider organizationProvider;

    @Override
    public EconomicGroupResponse execute(EconomicGroupRequest economicGroupRequest) {
        return (EconomicGroupResponse) Either.<BusinessException, Context>right(new Context(economicGroupRequest))
                .flatMap(this::checkForDuplicatedEconomicGroup)
                .flatMap(this::validateOrganizations)
                .flatMap(this::createEconomicGroup)
                .flatMap(this::validateEconomicGroup)
                .flatMap(this::saveEconomicGroup)
                .map(context -> context.economicGroup)
                .fold(this::throwBusinessException, EconomicGroupResponse::from);
    }

    private Either<? extends BusinessException, Context> saveEconomicGroup(Context context) {
        context.economicGroup = economicGroupRepository.save(context.economicGroup);
        return Either.right(context);
    }

    private Either<? extends BusinessException, Context> validateEconomicGroup(Context context) {
        return context.economicGroup.valid()
                .map(economicGroup -> context);
    }

    private Either<? extends BusinessException, Context> createEconomicGroup(Context context) {
        context.economicGroup = EconomicGroup.builder()
                .id(new TsidGenerator().newIdAsLong())
                .name(context.request.name())
                .description(context.request.description())
                .organizationIds(new HashSet<>(context.request.organizationIds()))
                .build();
        return Either.right(context);
    }

    private Either<? extends BusinessException, Context> validateOrganizations(Context context) {
        List<String> response = organizationProvider.getOrganizationsNotFound(context.request.organizationIds());
        if (response.isEmpty()) {
            return Either.right(context);
        }
        String responseAgregateList = String.join(",", response);
        String exceptionMessage = String.format(EconomicGroupConstants.ORGANIZATIONS_NOT_FOUND, responseAgregateList);
        return Either.left(new BusinessException(exceptionMessage));
    }

    private Either<? extends BusinessException, Context> checkForDuplicatedEconomicGroup(Context context) {
        return economicGroupRepository.existsByName(context.request.name()) ?
                Either.left(new BusinessException(EconomicGroupConstants.ECONOMIC_GROUP_ALREADY_EXISTS)) :
                Either.right(context);
    }

    private static class Context {
        public Context(EconomicGroupRequest request) {
            this.request = request;
        }

        public EconomicGroupRequest request;
        public EconomicGroup economicGroup;

    }
}
