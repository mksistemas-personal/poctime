package app.mkiniz.poctime.client.domain;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "client")
@Getter
@Setter
@Builder
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
