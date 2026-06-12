package app.mkiniz.poctime.product.domain;

import com.github.f4b6a3.tsid.Tsid;

public record ProductResponse(String id, String name, CategoryResponse category, String sku, String description) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                Tsid.from(product.getId()).toLowerCase(),
                product.getName(),
                CategoryResponse.from(product.getCategory()),
                product.getSku(),
                product.getDescription()
        );
    }


}
