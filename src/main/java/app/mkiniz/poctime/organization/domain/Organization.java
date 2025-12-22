package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.business.address.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "organization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE organization SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Organization {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_organization_person"))
    private Person person;

    @Embedded
    private Address address;

    @Column(name = "responsible_name", nullable = false, columnDefinition = "varchar(150)")
    private String responsibleName;

    @Column(name = "responsible_email", nullable = false, columnDefinition = "varchar(150)")
    private String responsibleEmail;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

}
