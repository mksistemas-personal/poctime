package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends ListCrudRepository<Person, Long>, PagingAndSortingRepository<Person, Long> {

    @Query("""
            SELECT * FROM person 
            WHERE document->>'identifier' = :identifier 
            AND document->>'type' = :type 
            AND deleted = false
            """)
    Optional<Person> findByDocumentIdentifierAndType(
            @Param("identifier") String identifier,
            @Param("type") String type
    );

    default Optional<Person> findByDocument(Document<?, ?> document) {
        if (document == null) return Optional.empty();
        String type = document.getClass().getSimpleName().toLowerCase().replace("document", "");
        return findByDocumentIdentifierAndType(document.identifier().toString(), type);
    }

}
