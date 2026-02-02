package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.organization.OrganizationProvider;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class UpdateEconomicGroupService implements
        UpdateBusinessUseCase<Tsid, EconomicGroupRequest, EconomicGroupResponse>,
        CommonEconomicGroupService {

    private final EconomicGroupRepository economicGroupRepository;
    private final OrganizationProvider organizationProvider;

    @Override
    public EconomicGroupResponse execute(Tsid id, EconomicGroupRequest economicGroupRequest) {
        return (EconomicGroupResponse) Either.<BusinessException, Context>right(new Context(id, economicGroupRequest))
                .flatMap(this::findEconomicGroup)
                .flatMap(this::verifyDuplicatedName)
                .flatMap(this::verifyOrganizationIds)
                .flatMap(this::updateAndSaveEntity)
                .fold(this::throwBusinessException, context -> EconomicGroupResponse.from(context.economicGroup));
    }

    private Either<? extends BusinessException, Context> updateAndSaveEntity(Context context) {
        context.economicGroup.setName(context.request.name());
        if (!context.isSameOrganizations())
            context.economicGroup.setOrganizationIds(new HashSet<>(context.request.organizationIds()));
        context.economicGroup.setDescription(context.request.description());
        context.economicGroup.updated();
        context.economicGroup = economicGroupRepository.save(context.economicGroup);
        return Either.right(context);
    }

    private Either<? extends BusinessException, Context> verifyOrganizationIds(Context context) {
        if (context.isSameOrganizations())
            return Either.right(context);
        return validateOrganizations(organizationProvider, context.request.organizationIds())
                .<Either<? extends BusinessException, Context>>map(Either::left)
                .orElse(Either.right(context));
    }

    private Either<? extends BusinessException, Context> verifyDuplicatedName(Context context) {
        if (context.isSameName())
            return Either.right(context);
        return economicGroupRepository.existsByName(context.request.name()) ?
                Either.left(new BusinessException(EconomicGroupConstants.ECONOMIC_GROUP_ALREADY_EXISTS)) :
                Either.right(context);
    }

    private Either<? extends BusinessException, ? extends Context> findEconomicGroup(Context context) {
        Optional<EconomicGroup> economicGroup = economicGroupRepository.findById(context.id.toLong());
        economicGroup.ifPresent(value -> context.economicGroup = value);
        return economicGroup.isPresent() ?
                Either.right(context) :
                Either.left(new BusinessException(EconomicGroupConstants.ECONOMIC_GROUP_NOT_FOUND));
    }

    private static class Context {
        public Tsid id;

        public Context(Tsid id, EconomicGroupRequest request) {
            this.id = id;
            this.request = request;
        }

        public EconomicGroupRequest request;
        public EconomicGroup economicGroup;

        public boolean isSameName() {
            return StringUtils.isBlank(request.name()) || request.name().equalsIgnoreCase(economicGroup.getName());
        }

        public boolean isSameOrganizations() {
            List<String> economicGroupList = economicGroup.getOrganizationIds().stream().sorted().toList();
            List<String> requestList = request.organizationIds().stream().sorted().toList();
            return economicGroupList.equals(requestList);
        }

    }
}
