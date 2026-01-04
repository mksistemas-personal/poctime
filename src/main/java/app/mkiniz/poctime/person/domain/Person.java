package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import cyclops.control.Either;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Objects;

@Entity
@Table(name = "person")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE person SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Person extends AbstractAggregateRoot<Person> implements EntityCreated, EntityUpdated, EntityDeleted {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document", columnDefinition = "jsonb", nullable = false)
    private Document<?, ?> document;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public PersonKindEnumeration whatKind() {
        if (Objects.isNull(document)) return PersonKindEnumeration.UNKNOWN;
        return switch (document.getClass().getSimpleName()) {
            case "CpfDocument" -> PersonKindEnumeration.CPF;
            case "CnpjDocument" -> PersonKindEnumeration.CNPJ;
            default -> PersonKindEnumeration.UNKNOWN;
        };
    }

    public void created() {
        this.registerEvent(new PersonAddedEvent(TsidGenerator.fromLongToString(id), name, document));
    }

    @Override
    public void updated() {
        this.registerEvent(new PersonUpdatedEvent(TsidGenerator.fromLongToString(id), name, document));
    }

    public void deleted() {
        this.registerEvent(new PersonDeletedEvent(TsidGenerator.fromLongToString(id), name, document));
    }

    public Either<BusinessException, Person> valid() {
        if (Objects.nonNull(document) && !document.isValid())
            return Either.left(new BusinessException(PersonConstants.DOCUMENT_INVALID));
        return Either.right(this);
    }
}
