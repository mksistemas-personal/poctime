package app.mkiniz.poctime.economicgroup.domain;

import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.HashSet;
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "economic_group_organization",
            joinColumns = @JoinColumn(name = "economic_group_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    @Builder.Default
    private Set<Organization> organizations = new HashSet<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

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
