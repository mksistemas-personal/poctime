package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
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

@Repository
@RequiredArgsConstructor
public class PersonJdbcRepository implements PersonRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Optional<Person> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM person WHERE id = :id AND deleted = false")
                .param("id", id)
                .query(this::mapRow)
                .optional();
    }

    public long count() {
        return jdbcClient.sql("SELECT count(*) FROM person WHERE deleted = false")
                .query(Long.class)
                .single();
    }

    public Slice<Person> findAll(Pageable pageable) {
        List<Person> people = jdbcClient.sql("SELECT * FROM person WHERE deleted = false LIMIT :limit OFFSET :offset")
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query(this::mapRow)
                .list();

        boolean hasNext = people.size() > pageable.getPageSize();
        if (hasNext) {
            people = people.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(people, pageable, hasNext);
    }

    public Slice<Person> findBySearchRequest(PersonSearchRequest request, Pageable pageable) {
        if (Objects.isNull(request) || (Objects.isNull(request.name()) && Objects.isNull(request.identifier()))) {
            return findAll(pageable);
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM person WHERE deleted = false");
        if (Objects.nonNull(request.name())) {
            sql.append(" AND name ILIKE :name");
        }
        if (Objects.nonNull(request.identifier())) {
            sql.append(" AND document->>'identifier' LIKE :identifier");
        }
        sql.append(" LIMIT :limit OFFSET :offset");

        JdbcClient.StatementSpec spec = jdbcClient.sql(sql.toString());
        if (Objects.nonNull(request.name())) {
            spec = spec.param("name", "%" + request.name() + "%");
        }
        if (Objects.nonNull(request.identifier())) {
            spec = spec.param("identifier", "%" + request.identifier() + "%");
        }

        List<Person> people = spec
                .param("limit", pageable.getPageSize() + 1)
                .param("offset", pageable.getOffset())
                .query(this::mapRow)
                .list();

        boolean hasNext = people.size() > pageable.getPageSize();
        if (hasNext) {
            people = people.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(people, pageable, hasNext);
    }

    public Optional<Person> findByDocumentIdentifierAndType(String identifier, String type) {
        return jdbcClient.sql("""
                        SELECT * FROM person 
                        WHERE document->>'identifier' = :identifier 
                        AND document->>'type' = :type 
                        AND deleted = false
                        """)
                .param("identifier", identifier)
                .param("type", type)
                .query(this::mapRow)
                .optional();
    }

    public Optional<Person> findByDocument(Document<?, ?> document) {
        if (document == null) return Optional.empty();
        String type = document.getClass().getSimpleName().toLowerCase().replace("document", "");
        return findByDocumentIdentifierAndType(document.identifier().toString(), type);
    }

    public Person save(Person person) {
        boolean exists = jdbcClient.sql("SELECT count(*) FROM person WHERE id = :id")
                .param("id", person.getId())
                .query(Integer.class)
                .single() > 0;

        if (exists) {
            update(person);
        } else {
            insert(person);
        }

        publishEvents(person);
        return person;
    }

    private void insert(Person person) {
        jdbcClient.sql("INSERT INTO person (id, name, document, deleted) VALUES (:id, :name, :document, :deleted)")
                .param("id", person.getId())
                .param("name", person.getName())
                .param("document", toJsonb(person.getDocument()))
                .param("deleted", person.isDeleted())
                .update();
    }

    private void update(Person person) {
        jdbcClient.sql("UPDATE person SET name = :name, document = :document, deleted = :deleted WHERE id = :id")
                .param("id", person.getId())
                .param("name", person.getName())
                .param("document", toJsonb(person.getDocument()))
                .param("deleted", person.isDeleted())
                .update();
    }

    public void delete(Person person) {
        jdbcClient.sql("UPDATE person SET deleted = true WHERE id = :id")
                .param("id", person.getId())
                .update();
        publishEvents(person);
    }

    private void publishEvents(Person person) {
        person.domainEvents().forEach(eventPublisher::publishEvent);
        person.clearDomainEvents();
    }

    private Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return Person.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .document(objectMapper.readValue(rs.getString("document"), Document.class))
                    .deleted(rs.getBoolean("deleted"))
                    .build();
        } catch (Exception e) {
            throw new SQLException("Error mapping Person", e);
        }
    }

    private PGobject toJsonb(Document<?, ?> document) {
        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(objectMapper.writeValueAsString(document));
            return pgObject;
        } catch (Exception e) {
            throw new RuntimeException("Error converting document to JSONB", e);
        }
    }
}
