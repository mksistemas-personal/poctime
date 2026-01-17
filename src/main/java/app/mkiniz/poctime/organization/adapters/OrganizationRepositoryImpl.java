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

@Repository
class OrganizationRepositoryImpl implements OrganizationProjectionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Slice<OrganizationProjectionResponse> findAllProjections(Pageable pageable) {
        String sql = """
                select
                    o.id,
                    p.name,
                    p.document->>'type' as type,
                    p.document->>'identifier' as identifier,
                    o.city,
                    p.id as person_id
                from
                    organization o inner join 
                        person p on o.person_id = p.id
                where
                   o.deleted = false
                """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize() + 1)
                .getResultList();

        List<OrganizationProjectionResponse> elements = results.stream()
                .map(row -> new OrganizationProjectionResponse(
                        Tsid.from((Long) row[0]).toLowerCase(),
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
