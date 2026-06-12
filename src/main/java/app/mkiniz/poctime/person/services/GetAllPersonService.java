package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.person.domain.PersonSearchRequest;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllPersonService implements GetAllBusinessUseCase<PersonSearchRequest, Maybe<Slice<PersonResponse>>> {

    private final PersonRepository personRepository;
    private final JdbcClient jdbcClient;

    @Override
    public Maybe<Slice<PersonResponse>> execute(Pageable pageable, @Nullable PersonSearchRequest request) {
        return Maybe.fromEval(later(() -> {
                    if (Objects.isNull(request) || (Objects.isNull(request.name()) && Objects.isNull(request.identifier()))) {
                        return personRepository.findAll(pageable);
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
                            .query(Person.class)
                            .list();

                    boolean hasNext = people.size() > pageable.getPageSize();
                    if (hasNext) {
                        people = people.subList(0, pageable.getPageSize());
                    }

                    return new SliceImpl<>(people, pageable, hasNext);
                }))
                .filter(Slice::hasContent)
                .map(people -> new SliceImpl<>(people.map(PersonResponse::fromPerson).toList(),
                        pageable,
                        people.hasNext()));
    }
}
