package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE product SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Product extends AbstractAggregateRoot<Product> implements EntityCreated, EntityUpdated, EntityDeleted {
    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(255)")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Override
    public void created() {
        // Implementação necessária pela interface EntityCreated
    }

    @Override
    public void updated() {
        // Implementação necessária pela interface EntityUpdated
    }

    @Override
    public void deleted() {
        // Implementação necessária pela interface EntityDeleted
    }
}
