package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.Immutable;

@Entity
@Immutable
@Getter
@Subselect("SELECT p.id, p.name, p.document FROM person p WHERE p.deleted = false and p.document->>'type' = 'cnpj'")
@Synchronize({"person"})
public class PersonCnpjView {

    @Id
    private Long id;

    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    private Document<?, ?> document;
}
