package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.domain.OrganizationProjectionRepository;
import app.mkiniz.poctime.organization.domain.OrganizationProjectionResponse;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
class OrganizationRepositoryImpl implements OrganizationProjectionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Slice<OrganizationProjectionResponse> findAllProjections(Pageable pageable, String documentType) {
        String sql = """
                select
                    case
                        when o.deleted then null
                        else o.id
                    end as organization_id,
                    p.name,
                    p.document->>'type' as type,
                    p.document->>'identifier' as identifier,
                    case
                        when o.deleted then null
                        else o.city
                    end as organization_city,
                    p.id as person_id
                from
                    person p left join
                    organization o on p.id = o.person_id
                where
                    p.deleted = false and
                    (cast(:documentType as varchar) is null or p.document->>'type' = :documentType)
                """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize() + 1)
                .setParameter("documentType", documentType)
                .getResultList();

        List<OrganizationProjectionResponse> elements = results.stream()
                .map(row -> new OrganizationProjectionResponse(
                        Objects.isNull(row[0]) ? null : Tsid.from((Long) row[0]).toLowerCase(),
                        Tsid.from((Long) row[5]).toLowerCase(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4]
                ))
                .toList()
                .subList(0, Math.min(results.size(), pageable.getPageSize()));
        return new SliceImpl<>(elements, pageable, results.size() > pageable.getPageSize());
    }
}
