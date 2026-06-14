package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import cyclops.control.Try;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends AbstractAggregateRoot<Organization> implements EntityCreated, EntityUpdated, EntityDeleted {

    private Long id;

    private Person person;

    private Address address;

    private Person responsiblePerson;

    private String responsibleEmail;

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

    public Either<BusinessException, Organization> valid() {
        return Try.withCatch(() -> {
                    Objects.requireNonNull(person, OrganizationConstants.PERSON_NOT_NULL);
                    Objects.requireNonNull(address, OrganizationConstants.ADDRESS_NOT_NULL);
                    Objects.requireNonNull(responsiblePerson, OrganizationConstants.RESPONSIBLE_PERSON_NOT_NULL);
                    Objects.requireNonNull(responsibleEmail, OrganizationConstants.RESPONSIBLE_EMAIL_NOT_NULL);
                    return this;
                }, NullPointerException.class)
                .toEither()
                .mapLeft(e -> new BusinessException(e.getMessage()));
    }

    @Override
    public void created() {
        this.registerEvent(OrganizationAddedEvent.builder()
                .organizationId(Tsid.from(this.getId()))
                .responsibleId(Tsid.from(responsiblePerson.getId()))
                .responsibleEmail(responsibleEmail)
                .address(address)
                .build());
    }

    @Override
    public void deleted() {
        this.registerEvent(OrganizationDeletedEvent.builder()
                .organizationId(Tsid.from(this.getId()))
                .responsibleId(Tsid.from(responsiblePerson.getId()))
                .responsibleEmail(responsibleEmail)
                .address(address)
                .build());
    }

    @Override
    public void updated() {
        this.registerEvent(OrganizationUpdatedEvent.builder()
                .organizationId(Tsid.from(this.getId()))
                .responsibleId(Tsid.from(responsiblePerson.getId()))
                .responsibleEmail(responsibleEmail)
                .address(address)
                .build());
    }

    public boolean isPersonAndResponsibleSameCountry() {
        return getPersonCountry().equals(getResponsibleCountry());
    }

    private String getResponsibleCountry() {
        return responsiblePerson.getDocument().getCountry();
    }

    private String getPersonCountry() {
        return person.getDocument().getCountry();
    }

}
