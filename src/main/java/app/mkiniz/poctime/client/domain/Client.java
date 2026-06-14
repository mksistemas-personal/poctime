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
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client extends AbstractAggregateRoot<Client> implements EntityCreated, EntityUpdated, EntityDeleted {
    private Long id;

    private Person person;

    private Address address;

    private String clientEmail;

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
