package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationProjectionResponse;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationSearchRequest;
import app.mkiniz.poctime.person.domain.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.Tsid;
import lombok.RequiredArgsConstructor;
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

@Repository
@RequiredArgsConstructor
class OrganizationJdbcRepository implements OrganizationRepository {

    private final JdbcClient jdbcClient;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Organization> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT o.*, 
                               p.name as person_name, p.document as person_document, p.deleted as person_deleted,
                               rp.name as resp_name, rp.document as resp_document, rp.deleted as resp_deleted
                        FROM organization o 
                        JOIN person p ON o.person_id = p.id 
                        JOIN person rp ON o.responsible_id = rp.id
                        WHERE o.id = :id AND o.deleted = false
                        """)
                .param("id", id)
                .query(this::mapRowWithPerson)
                .optional();
    }

    @Override
    public long count() {
        return jdbcClient.sql("SELECT count(*) FROM organization WHERE deleted = false")
                .query(Long.class)
                .single();
    }

    @Override
    public Optional<Organization> findByPersonId(Long personId) {
        return jdbcClient.sql("""
                        SELECT o.*, 
                               p.name as person_name, p.document as person_document, p.deleted as person_deleted,
                               rp.name as resp_name, rp.document as resp_document, rp.deleted as resp_deleted
                        FROM organization o 
                        JOIN person p ON o.person_id = p.id 
                        JOIN person rp ON o.responsible_id = rp.id
                        WHERE o.person_id = :personId AND o.deleted = false
                        """)
                .param("personId", personId)
                .query(this::mapRowWithPerson)
                .optional();
    }

    @Override
    public boolean existsByPersonIdOrResponsiblePersonId(Long personId, Long responsibleId) {
        return jdbcClient.sql("SELECT count(*) FROM organization WHERE (person_id = :personId OR responsible_id = :responsibleId) AND deleted = false")
                .param("personId", personId)
                .param("responsibleId", responsibleId)
                .query(Integer.class)
                .single() > 0;
    }

    @Override
    public Slice<Organization> findAll(OrganizationSearchRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("""
                SELECT o.*, 
                       p.name as person_name, p.document as person_document, p.deleted as person_deleted,
                       rp.name as resp_name, rp.document as resp_document, rp.deleted as resp_deleted
                FROM organization o 
                JOIN person p ON o.person_id = p.id 
                JOIN person rp ON o.responsible_id = rp.id
                WHERE o.deleted = false
                """);

        if (Objects.nonNull(request)) {
            if (Objects.nonNull(request.name())) {
                sql.append(" AND p.name ILIKE :name");
            }
            if (Objects.nonNull(request.respName())) {
                sql.append(" AND rp.name ILIKE :respName");
            }
            if (Objects.nonNull(request.responsibleEmail())) {
                sql.append(" AND o.responsible_email ILIKE :responsibleEmail");
            }
            if (Objects.nonNull(request.street())) {
                sql.append(" AND o.street ILIKE :street");
            }
            if (Objects.nonNull(request.city())) {
                sql.append(" AND o.city ILIKE :city");
            }
            if (Objects.nonNull(request.stateCode())) {
                sql.append(" AND o.state_code = :stateCode");
            }
        }

        sql.append(" LIMIT :limit OFFSET :offset");

        JdbcClient.StatementSpec spec = jdbcClient.sql(sql.toString());
        if (Objects.nonNull(request)) {
            if (Objects.nonNull(request.name())) spec = spec.param("name", "%" + request.name() + "%");
            if (Objects.nonNull(request.respName())) spec = spec.param("respName", "%" + request.respName() + "%");
            if (Objects.nonNull(request.responsibleEmail()))
                spec = spec.param("responsibleEmail", "%" + request.responsibleEmail() + "%");
            if (Objects.nonNull(request.street())) spec = spec.param("street", "%" + request.street() + "%");
            if (Objects.nonNull(request.city())) spec = spec.param("city", "%" + request.city() + "%");
            if (Objects.nonNull(request.stateCode())) spec = spec.param("stateCode", request.stateCode());
        }

        List<Organization> organizations = spec
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query(this::mapRowWithPerson)
                .list();

        boolean hasNext = organizations.size() > pageable.getPageSize();
        if (hasNext) {
            organizations = organizations.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(organizations, pageable, hasNext);
    }

    @Override
    public Slice<Organization> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public List<Long> findIdsByNotInList(Long[] ids) {
        return jdbcClient.sql("""
                        SELECT input_id FROM unnest(cast(:ids as bigint[])) as input_id 
                        WHERE NOT EXISTS (SELECT 1 FROM organization o WHERE o.id = input_id AND o.deleted = false)
                        """)
                .param("ids", ids)
                .query(Long.class)
                .list();
    }

    @Override
    public Organization save(Organization organization) {
        jdbcClient.sql("""
                        INSERT INTO organization (id, person_id, street, number, neighborhood, complement, city, state_code, country, zip_code, responsible_id, responsible_email, deleted)
                        VALUES (:id, :person_id, :street, :number, :neighborhood, :complement, :city, :state_code, :country_code, :zip_code, :responsible_id, :responsible_email, :deleted)
                        ON CONFLICT (id) DO UPDATE SET
                            person_id = EXCLUDED.person_id,
                            street = EXCLUDED.street,
                            number = EXCLUDED.number,
                            neighborhood = EXCLUDED.neighborhood,
                            complement = EXCLUDED.complement,
                            city = EXCLUDED.city,
                            state_code = EXCLUDED.state_code,
                            country = EXCLUDED.country,
                            zip_code = EXCLUDED.zip_code,
                            responsible_id = EXCLUDED.responsible_id,
                            responsible_email = EXCLUDED.responsible_email,
                            deleted = EXCLUDED.deleted
                        """)
                .param("id", organization.getId())
                .param("person_id", organization.getPerson().getId())
                .param("street", organization.getAddress().street())
                .param("number", organization.getAddress().number())
                .param("neighborhood", organization.getAddress().neighborhood())
                .param("complement", organization.getAddress().complement())
                .param("city", organization.getAddress().city())
                .param("state_code", organization.getAddress().stateCode())
                .param("country_code", organization.getAddress().country())
                .param("zip_code", organization.getAddress().zipCode())
                .param("responsible_id", organization.getResponsiblePerson().getId())
                .param("responsible_email", organization.getResponsibleEmail())
                .param("deleted", organization.isDeleted())
                .update();

        publishEvents(organization);
        return organization;
    }

    @Override
    public void delete(Organization organization) {
        jdbcClient.sql("UPDATE organization SET deleted = true WHERE id = :id")
                .param("id", organization.getId())
                .update();
        publishEvents(organization);
    }

    private void publishEvents(Organization organization) {
        organization.domainEvents().forEach(eventPublisher::publishEvent);
        organization.clearDomainEvents();
    }

    private Organization mapRowWithPerson(ResultSet rs, int rowNum) throws SQLException {
        Person person;
        Person responsiblePerson;
        try {
            person = Person.builder()
                    .id(rs.getLong("person_id"))
                    .name(rs.getString("person_name"))
                    .document(objectMapper.readValue(rs.getString("person_document"), Document.class))
                    .deleted(rs.getBoolean("person_deleted"))
                    .build();

            responsiblePerson = Person.builder()
                    .id(rs.getLong("responsible_id"))
                    .name(rs.getString("resp_name"))
                    .document(objectMapper.readValue(rs.getString("resp_document"), Document.class))
                    .deleted(rs.getBoolean("resp_deleted"))
                    .build();
        } catch (Exception e) {
            throw new SQLException("Error mapping Person in Organization", e);
        }

        return Organization.builder()
                .id(rs.getLong("id"))
                .person(person)
                .address(Address.builder()
                        .street(rs.getString("street"))
                        .number(rs.getString("number"))
                        .neighborhood(rs.getString("neighborhood"))
                        .complement(rs.getString("complement"))
                        .city(rs.getString("city"))
                        .stateCode(rs.getString("state_code"))
                        .country(rs.getString("country_code"))
                        .zipCode(rs.getString("zip_code"))
                        .build())
                .responsiblePerson(responsiblePerson)
                .responsibleEmail(rs.getString("responsible_email"))
                .deleted(rs.getBoolean("deleted"))
                .build();
    }

    @Override
    public Slice<OrganizationProjectionResponse> findAllProjections(Pageable pageable, String documentType) {
        String sql = """
                select
                    o.id,
                    p.name,
                    p.document->>'type' as type,
                    p.document->>'identifier' as identifier,
                    o.city,
                    p.id as person_id
                from
                    person p left join
                    organization o on p.id = o.person_id and o.deleted = false
                where
                    p.deleted = false and
                    (:documentType is null or p.document->>'type' = :documentType)
                LIMIT :limit OFFSET :offset
                """;

        List<OrganizationProjectionResponse> elements = jdbcClient.sql(sql)
                .param("documentType", documentType)
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query((rs, rowNum) -> new OrganizationProjectionResponse(
                        Objects.isNull(rs.getObject("id")) ? null : Tsid.from(rs.getLong("id")).toLowerCase(),
                        Tsid.from(rs.getLong("person_id")).toLowerCase(),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("identifier"),
                        rs.getString("city")
                ))
                .list();

        boolean hasNext = elements.size() > pageable.getPageSize();
        if (hasNext) {
            elements = elements.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(elements, pageable, hasNext);
    }
}
