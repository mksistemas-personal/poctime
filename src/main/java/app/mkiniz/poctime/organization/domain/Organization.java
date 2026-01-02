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
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Objects;

@Entity
@Table(name = "organization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE organization SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Organization extends AbstractAggregateRoot<Person> implements EntityCreated, EntityUpdated, EntityDeleted {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_organization_person"))
    private Person person;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", foreignKey = @ForeignKey(name = "fk_organization_responsible"))
    private Person responsiblePerson;

    @Column(name = "responsible_email", columnDefinition = "varchar(255)")
    private String responsibleEmail;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

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
                .organizationId(Tsid.from(this.person.getId()))
                .responsibleId(Tsid.from(responsiblePerson.getId()))
                .responsibleEmail(responsibleEmail)
                .address(address)
                .build());
    }

    @Override
    public void deleted() {

    }

    @Override
    public void updated() {

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
