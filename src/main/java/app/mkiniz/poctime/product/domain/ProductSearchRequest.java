package app.mkiniz.poctime.product.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductSearchRequest {
    String name;
    String category;
    String sku;
}
