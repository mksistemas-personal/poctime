package app.mkiniz.poctime.economicgroup.services;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Expression;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllEconomicGroupService implements GetAllBusinessUseCase<String, Maybe<Slice<EconomicGroupResponse>>> {

    private final EconomicGroupRepository repository;

    @Override
    public Maybe<Slice<EconomicGroupResponse>> execute(Pageable pageable, @Nullable String term) {

        Optional<Specification<EconomicGroup>> economicGroupSpecification =
                Optional.ofNullable(term).map(EconomicGroupSpecifications::textSearch);

        return Maybe.fromEval(later(() -> economicGroupSpecification
                        .map(groupSpecification -> repository.findAll(groupSpecification, pageable))
                        .orElseGet(() -> repository.findAll(pageable))))
                .filter(Slice::hasContent)
                .map(groups ->
                        new SliceImpl<>(groups.map(EconomicGroupResponse::from).toList(),
                                pageable,
                                groups.hasNext()));
    }

    public static class EconomicGroupSpecifications {

        public static Specification<EconomicGroup> textSearch(String term) {
            return (root, query, cb) -> {
                if (term == null || term.isBlank()) {
                    return cb.conjunction();
                }
                Expression<Object> tsQuery = cb.function(
                        "websearch_to_tsquery",
                        Object.class,
                        cb.literal("portuguese"),
                        cb.lower(cb.literal(term))
                );

                Expression<Object> tsQuerySimple = cb.function(
                        "to_tsquery",
                        Object.class,
                        cb.literal("simple"),
                        cb.lower(cb.literal(term))
                );

                return cb.or(
                        cb.isTrue(cb.function("ts_match", Boolean.class, root.get("searchVector"), tsQuery)),
                        cb.isTrue(cb.function("ts_match", Boolean.class, root.get("searchVector"), tsQuerySimple))
                );
            };
        }
    }
}
