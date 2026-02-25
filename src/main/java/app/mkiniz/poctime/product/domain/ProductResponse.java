package app.mkiniz.poctime.product.domain;

public record ProductResponse(String id, String name, Category category) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId().toString(),
                product.getName(),
                product.getCategory()
        );
    }
}
