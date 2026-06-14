package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface PersonRepository {
    Optional<Person> findById(Long id);

    long count();

    Slice<Person> findAll(Pageable pageable);

    Slice<Person> findBySearchRequest(PersonSearchRequest request, Pageable pageable);

    Optional<Person> findByDocumentIdentifierAndType(String identifier, String type);

    Optional<Person> findByDocument(Document<?, ?> document);

    Person save(Person person);

    void delete(Person person);
}
