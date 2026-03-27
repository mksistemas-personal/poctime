package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.category.CategoryEvent;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxData;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxEvent;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "sku", columnDefinition = "varchar(100)")
    private String sku;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductTaxData> taxDataHistory;

    public Optional<ProductTaxData> getTaxDataForDate(LocalDate date) {
        if (Objects.isNull(taxDataHistory)) {
            return Optional.empty();
        }
        return taxDataHistory.stream()
                .filter(tax -> !tax.isBefore(date) && tax.isAfter(date))
                .findFirst();
    }

    @Override
    public void created() {
        this.registerEvent(
                ProductAddedEvent
                        .builder()
                        .productId(Tsid.from(this.id))
                        .name(this.name)
                        .description(this.description)
                        .sku(this.sku)
                        .category(CategoryEvent.from(this.category))
                        .taxData(this.taxDataHistory != null ? this.taxDataHistory.stream().map(ProductTaxEvent::from).toList() : List.of())
                        .build());
    }

    @Override
    public void updated() {
        this.registerEvent(
                ProductUpdatedEvent
                        .builder()
                        .productId(Tsid.from(this.id))
                        .name(this.name)
                        .description(this.description)
                        .sku(this.sku)
                        .category(CategoryEvent.from(this.category))
                        .taxData(this.taxDataHistory != null ? this.taxDataHistory.stream().map(ProductTaxEvent::from).toList() : List.of())
                        .build());
    }

    @Override
    public void deleted() {
        this.registerEvent(
                ProductDeletedEvent
                        .builder()
                        .productId(Tsid.from(this.id))
                        .name(this.name)
                        .description(this.description)
                        .sku(this.sku)
                        .category(CategoryEvent.from(this.category))
                        .taxData(this.taxDataHistory != null ? this.taxDataHistory.stream().map(ProductTaxEvent::from).toList() : List.of())
                        .build());
    }
}
