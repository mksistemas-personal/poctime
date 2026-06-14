package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.category.CategoryEvent;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxEvent;
import app.mkiniz.poctime.shared.business.EntityCreated;
import app.mkiniz.poctime.shared.business.EntityDeleted;
import app.mkiniz.poctime.shared.business.EntityUpdated;
import com.github.f4b6a3.tsid.Tsid;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product implements EntityCreated, EntityUpdated, EntityDeleted {

    private Long id;

    private String name;

    private Category category;

    private String description;

    private String sku;

    private boolean deleted = false;

    private List<ProductTaxData> taxDataHistory;

    private final List<Object> domainEvents = new ArrayList<>();

    protected void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> domainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

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
