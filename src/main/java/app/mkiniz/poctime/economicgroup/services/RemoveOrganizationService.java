package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.RemoveOrganizationUseCase;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Eval;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@Transactional
@AllArgsConstructor
class RemoveOrganizationService implements RemoveOrganizationUseCase {

    private final EconomicGroupRepository economicGroupRepository;

    @Override
    public RemoveOrganizationResponse execute(RemoveOrganizationRequest request) {
        return Eval.now(new Context(request))
                .map(this::createIds)
                .map(this::findAllGroups)
                .map(this::removeOrganizations)
                .map(context -> new RemoveOrganizationResponse(context.economicGroupIds))
                .get();
    }

    private Context removeOrganizations(Context context) {
        context.groups.forEach((group) -> {
            group.getOrganizationIds().removeIf(context.ids::contains);
            context.economicGroupIds.add(Tsid.from(group.getId()));
            group.updated();
            economicGroupRepository.save(group);
        });
        return context;
    }

    private Context findAllGroups(Context context) {
        context.groups = context.ids.stream()
                .flatMap(orgId -> economicGroupRepository.findAllByOrganizationId(orgId).stream())
                .toList();
        return context;
    }

    private Context createIds(Context context) {
        context.ids = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        context.ids.addAll(context.request
                .organizationIds()
                .stream()
                .map(Tsid::toLowerCase)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList());
        return context;
    }

    private static class Context {
        public RemoveOrganizationRequest request;
        public Set<String> ids;
        public List<EconomicGroup> groups;
        public List<Tsid> economicGroupIds;

        public Context(RemoveOrganizationRequest request) {
            this.request = request;
            economicGroupIds = new LinkedList<>();
        }
    }

}
