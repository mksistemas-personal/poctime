package app.mkiniz.poctime.client.adapters;

import app.mkiniz.poctime.client.domain.ClientProjectionRepository;
import app.mkiniz.poctime.client.domain.ClientProjectionResponse;
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
class ClientRepositoryImpl implements ClientProjectionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Slice<ClientProjectionResponse> findAllProjections(Pageable pageable) {
        String sql = """
                select
                   c.id,
                   p.name,
                   p.document->>'type' as type,
                   p.document->>'identifier' as identifier,
                   c.city,
                   p.id as person_id
                from
                   person p left join
                   client c on p.id = c.person_id and c.deleted = false
                where
                   p.deleted = false
                """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize() + 1)
                .getResultList();

        List<ClientProjectionResponse> elements = results.stream()
                .map(row -> new ClientProjectionResponse(
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
