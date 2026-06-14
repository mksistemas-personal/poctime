package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import cyclops.control.Either;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Person extends AbstractAggregateRoot<Person> implements EntityCreated, EntityUpdated, EntityDeleted {

    private Long id;

    private String name;

    private Document<?, ?> document;

    public Document<?, ?> getDocument() {
        return document;
    }

    @Builder.Default
    private boolean deleted = false;

    @Override
    public Collection<Object> domainEvents() {
        return super.domainEvents();
    }

    @Override
    public void clearDomainEvents() {
        super.clearDomainEvents();
    }

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
