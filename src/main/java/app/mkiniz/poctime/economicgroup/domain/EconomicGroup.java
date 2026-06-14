package app.mkiniz.poctime.economicgroup.domain;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import cyclops.control.Either;
import cyclops.control.Try;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EconomicGroup extends AbstractAggregateRoot<EconomicGroup> implements EntityCreated, EntityUpdated, EntityDeleted {

    private Long id;

    private String name;

    private String description;

    private Set<String> organizationIds;

    @Builder.Default
    private boolean deleted = false;

    private Object searchVector;

    @Override
    public Collection<Object> domainEvents() {
        return super.domainEvents();
    }

    @Override
    public void clearDomainEvents() {
        super.clearDomainEvents();
    }

    public Either<BusinessException, EconomicGroup> valid() {
        return Try.withCatch(() -> {
                    Objects.requireNonNull(name, EconomicGroupConstants.NAME_NOT_BLANK);
                    return this;
                }, NullPointerException.class)
                .toEither()
                .mapLeft(e -> new BusinessException(e.getMessage()));
    }

    @Override
    public void created() {
        this.registerEvent(EconomicGroupAddedEvent.builder()
                .id(this.getId().toString())
                .name(this.getName())
                .description(this.getDescription())
                .organizationIds(this.getOrganizationIds().stream().toList())
                .build());
    }

    @Override
    public void deleted() {
        this.registerEvent(EconomicGroupDeletedEvent.builder()
                .id(this.getId().toString())
                .name(this.getName())
                .description(this.getDescription())
                .organizationIds(this.getOrganizationIds().stream().toList())
                .build());
    }

    @Override
    public void updated() {
        this.registerEvent(EconomicGroupUpdatedEvent.builder()
                .id(this.getId().toString())
                .name(this.getName())
                .description(this.getDescription())
                .organizationIds(this.getOrganizationIds().stream().toList())
                .build());
    }

}
