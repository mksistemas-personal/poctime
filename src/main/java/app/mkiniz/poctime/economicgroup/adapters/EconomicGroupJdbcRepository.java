package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroup;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRepository;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupSearchRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
class EconomicGroupJdbcRepository implements EconomicGroupRepository {

    private final JdbcClient jdbcClient;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public boolean existsByName(String name) {
        return jdbcClient.sql("SELECT count(*) FROM economicgroup WHERE name = :name AND deleted = false")
                .param("name", name)
                .query(Integer.class)
                .single() > 0;
    }

    @Override
    public long count() {
        return jdbcClient.sql("SELECT count(*) FROM economicgroup WHERE deleted = false")
                .query(Long.class)
                .single();
    }

    @Override
    public Slice<EconomicGroup> findAll(EconomicGroupSearchRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT * FROM economicgroup WHERE deleted = false");

        if (Objects.nonNull(request) && Objects.nonNull(request.term()) && !request.term().isBlank()) {
            sql.append("""
                     AND (
                        search_vector @@ websearch_to_tsquery('portuguese', :term) OR
                        search_vector @@ to_tsquery('simple', :term)
                    )
                    """);
        }

        sql.append(" LIMIT :limit OFFSET :offset");

        JdbcClient.StatementSpec spec = jdbcClient.sql(sql.toString());
        if (Objects.nonNull(request) && Objects.nonNull(request.term()) && !request.term().isBlank()) {
            spec = spec.param("term", request.term());
        }

        List<EconomicGroup> groups = spec
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query(this::mapRow)
                .list();

        boolean hasNext = groups.size() > pageable.getPageSize();
        if (hasNext) {
            groups = groups.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(groups, pageable, hasNext);
    }

    @Override
    public Slice<EconomicGroup> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Optional<EconomicGroup> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM economicgroup WHERE id = :id AND deleted = false")
                .param("id", id)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public List<EconomicGroup> findAllByOrganizationId(String organizationId) {
        return jdbcClient.sql("""
                        SELECT * FROM economicgroup 
                        WHERE deleted = false AND search_vector @@ to_tsquery('simple', :orgId)
                        """)
                .param("orgId", organizationId)
                .query(this::mapRow)
                .list();
    }

    @Override
    public EconomicGroup save(EconomicGroup economicGroup) {
        jdbcClient.sql("""
                        INSERT INTO economicgroup (id, name, description, "organization-ids", deleted)
                        VALUES (:id, :name, :description, :organizationIds, :deleted)
                        ON CONFLICT (id) DO UPDATE SET
                            name = EXCLUDED.name,
                            description = EXCLUDED.description,
                            "organization-ids" = EXCLUDED."organization-ids",
                            deleted = EXCLUDED.deleted
                        """)
                .param("id", economicGroup.getId())
                .param("name", economicGroup.getName())
                .param("description", economicGroup.getDescription())
                .param("organizationIds", toJsonb(economicGroup.getOrganizationIds()))
                .param("deleted", economicGroup.isDeleted())
                .update();

        publishEvents(economicGroup);
        return economicGroup;
    }

    @Override
    public void delete(EconomicGroup economicGroup) {
        jdbcClient.sql("UPDATE economicgroup SET deleted = true WHERE id = :id")
                .param("id", economicGroup.getId())
                .update();
        publishEvents(economicGroup);
    }

    private void publishEvents(EconomicGroup economicGroup) {
        economicGroup.domainEvents().forEach(eventPublisher::publishEvent);
        economicGroup.clearDomainEvents();
    }

    private EconomicGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return EconomicGroup.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .organizationIds(objectMapper.readValue(rs.getString("organization-ids"), new TypeReference<Set<String>>() {
                    }))
                    .deleted(rs.getBoolean("deleted"))
                    .build();
        } catch (Exception e) {
            throw new SQLException("Error mapping EconomicGroup", e);
        }
    }

    private PGobject toJsonb(Object value) {
        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(objectMapper.writeValueAsString(value));
            return pgObject;
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSONB", e);
        }
    }
}
