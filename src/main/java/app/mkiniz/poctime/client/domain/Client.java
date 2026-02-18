package app.mkiniz.poctime.client.domain;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.client.ClientConstants;
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
@Table(name = "client")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE client SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Client extends AbstractAggregateRoot<Client> implements EntityCreated, EntityUpdated, EntityDeleted {
    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "fk_client_person"))
    private Person person;

    @Embedded
    private Address address;

    @Column(name = "client_email", columnDefinition = "varchar(255)")
    private String clientEmail;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public Either<BusinessException, Client> valid() {
        return Try.withCatch(() -> {
                    Objects.requireNonNull(person, ClientConstants.PERSON_NOT_NULL);
                    Objects.requireNonNull(address, ClientConstants.ADDRESS_NOT_NULL);
                    Objects.requireNonNull(clientEmail, ClientConstants.CLIENT_EMAIL_NOT_NULL);
                    return this;
                }, NullPointerException.class)
                .toEither()
                .mapLeft(e -> new BusinessException(e.getMessage()));
    }

    @Override
    public void created() {
        this.registerEvent(ClientAddedEvent.builder()
                .clientId(Tsid.from(this.getId()))
                .person(PersonResponse.from(this.getPerson()))
                .clientEmail(this.getClientEmail())
                .address(this.getAddress())
                .build());
    }

    @Override
    public void deleted() {
        this.registerEvent(ClientDeletedEvent.builder()
                .clientId(Tsid.from(this.getId()))
                .person(PersonResponse.from(this.getPerson()))
                .clientEmail(this.getClientEmail())
                .address(this.getAddress())
                .build());
    }

    @Override
    public void updated() {
        this.registerEvent(ClientUpdatedEvent.builder()
                .clientId(Tsid.from(this.getId()))
                .person(PersonResponse.from(this.getPerson()))
                .clientEmail(this.getClientEmail())
                .address(this.getAddress()));
    }
}
