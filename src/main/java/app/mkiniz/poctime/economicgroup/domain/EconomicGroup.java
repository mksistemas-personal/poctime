package app.mkiniz.poctime.economicgroup.domain;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import cyclops.control.Either;
import cyclops.control.Try;
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
import java.util.Set;

@Entity
@Table(name = "economicgroup")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE economicgroup SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class EconomicGroup extends AbstractAggregateRoot<EconomicGroup> implements EntityCreated, EntityUpdated, EntityDeleted {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    private String name;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"organization-ids\"", columnDefinition = "jsonb", nullable = false)
    private Set<String> organizationIds;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Generated
    @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
    private Object searchVector;

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
    }

    @Override
    public void deleted() {
    }

    @Override
    public void updated() {
    }

}
