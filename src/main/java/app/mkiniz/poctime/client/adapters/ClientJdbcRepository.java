package app.mkiniz.poctime.client.adapters;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientProjectionResponse;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientSearchRequest;
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
class ClientJdbcRepository implements ClientRepository {

    private final JdbcClient jdbcClient;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Client> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT c.*, p.name as person_name, p.document as person_document, p.deleted as person_deleted 
                        FROM client c 
                        JOIN person p ON c.person_id = p.id 
                        WHERE c.id = :id AND c.deleted = false
                        """)
                .param("id", id)
                .query(this::mapRowWithPerson)
                .optional();
    }

    @Override
    public long count() {
        return jdbcClient.sql("SELECT count(*) FROM client WHERE deleted = false")
                .query(Long.class)
                .single();
    }

    @Override
    public Optional<Client> findByPersonId(Long personId) {
        return jdbcClient.sql("""
                        SELECT c.*, p.name as person_name, p.document as person_document, p.deleted as person_deleted 
                        FROM client c 
                        JOIN person p ON c.person_id = p.id 
                        WHERE c.person_id = :personId AND c.deleted = false
                        """)
                .param("personId", personId)
                .query(this::mapRowWithPerson)
                .optional();
    }

    @Override
    public boolean existsByPersonId(Long personId) {
        return jdbcClient.sql("SELECT count(*) FROM client WHERE person_id = :personId AND deleted = false")
                .param("personId", personId)
                .query(Integer.class)
                .single() > 0;
    }

    @Override
    public Slice<Client> findAll(Pageable pageable) {
        return findBySearchRequest(null, pageable);
    }

    @Override
    public Slice<Client> findBySearchRequest(ClientSearchRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("""
                SELECT c.*, p.name as person_name, p.document as person_document, p.deleted as person_deleted 
                FROM client c 
                JOIN person p ON c.person_id = p.id 
                WHERE c.deleted = false
                """);

        if (Objects.nonNull(request)) {
            if (Objects.nonNull(request.name())) {
                sql.append(" AND p.name ILIKE :name");
            }
            if (Objects.nonNull(request.street())) {
                sql.append(" AND c.street ILIKE :street");
            }
            if (Objects.nonNull(request.city())) {
                sql.append(" AND c.city ILIKE :city");
            }
            if (Objects.nonNull(request.stateCode())) {
                sql.append(" AND c.state_code = :stateCode");
            }
            if (Objects.nonNull(request.email())) {
                sql.append(" AND c.client_email ILIKE :email");
            }
        }

        sql.append(" LIMIT :limit OFFSET :offset");

        JdbcClient.StatementSpec spec = jdbcClient.sql(sql.toString());
        if (Objects.nonNull(request)) {
            if (Objects.nonNull(request.name())) spec = spec.param("name", "%" + request.name() + "%");
            if (Objects.nonNull(request.street())) spec = spec.param("street", "%" + request.street() + "%");
            if (Objects.nonNull(request.city())) spec = spec.param("city", "%" + request.city() + "%");
            if (Objects.nonNull(request.stateCode())) spec = spec.param("stateCode", request.stateCode());
            if (Objects.nonNull(request.email())) spec = spec.param("email", "%" + request.email() + "%");
        }

        List<Client> clients = spec
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query(this::mapRowWithPerson)
                .list();

        boolean hasNext = clients.size() > pageable.getPageSize();
        if (hasNext) {
            clients = clients.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(clients, pageable, hasNext);
    }

    @Override
    public Client save(Client client) {
        jdbcClient.sql("""
                        INSERT INTO client (id, person_id, street, number, neighborhood, complement, city, state_code, country, zip_code, client_email, deleted)
                        VALUES (:id, :person_id, :street, :number, :neighborhood, :complement, :city, :state_code, :country_code, :zip_code, :client_email, :deleted)
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
                            client_email = EXCLUDED.client_email,
                            deleted = EXCLUDED.deleted
                        """)
                .param("id", client.getId())
                .param("person_id", client.getPerson().getId())
                .param("street", client.getAddress().street())
                .param("number", client.getAddress().number())
                .param("neighborhood", client.getAddress().neighborhood())
                .param("complement", client.getAddress().complement())
                .param("city", client.getAddress().city())
                .param("state_code", client.getAddress().stateCode())
                .param("country_code", client.getAddress().country())
                .param("zip_code", client.getAddress().zipCode())
                .param("client_email", client.getClientEmail())
                .param("deleted", client.isDeleted())
                .update();

        publishEvents(client);
        return client;
    }

    @Override
    public void delete(Client client) {
        jdbcClient.sql("UPDATE client SET deleted = true WHERE id = :id")
                .param("id", client.getId())
                .update();
        publishEvents(client);
    }

    private void publishEvents(Client client) {
        client.domainEvents().forEach(eventPublisher::publishEvent);
        client.clearDomainEvents();
    }

    private Client mapRowWithPerson(ResultSet rs, int rowNum) throws SQLException {
        Person person;
        try {
            person = Person.builder()
                    .id(rs.getLong("person_id"))
                    .name(rs.getString("person_name"))
                    .document(objectMapper.readValue(rs.getString("person_document"), Document.class))
                    .deleted(rs.getBoolean("person_deleted"))
                    .build();
        } catch (Exception e) {
            throw new SQLException("Error mapping Person in Client", e);
        }

        return Client.builder()
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
                .clientEmail(rs.getString("client_email"))
                .deleted(rs.getBoolean("deleted"))
                .build();
    }

    @Override
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
                LIMIT :limit OFFSET :offset
                """;

        List<ClientProjectionResponse> elements = jdbcClient.sql(sql)
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query((rs, rowNum) -> new ClientProjectionResponse(
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
