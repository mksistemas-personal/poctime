package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.domain.category.CategoryEvent;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxEvent;
import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductDeletedEvent(Tsid productId, String name, String description, String sku, CategoryEvent category,
                                  List<ProductTaxEvent> taxData) {
}
