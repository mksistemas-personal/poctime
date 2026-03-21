package app.mkiniz.poctime.product.domain;

import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

@Builder
public record ProductAddedEvent(Tsid productId, String name, String description, String sku, CategoryEvent category) {
}
