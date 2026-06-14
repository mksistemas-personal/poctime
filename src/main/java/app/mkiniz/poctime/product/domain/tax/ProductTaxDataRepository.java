package app.mkiniz.poctime.product.domain.tax;

import app.mkiniz.poctime.product.domain.Product;

import java.util.Optional;

public interface ProductTaxDataRepository {
    Optional<ProductTaxData> findById(Long id);

    ProductTaxData save(ProductTaxData productTaxData);

    void deleteById(Long id);

    Optional<ProductTaxData> findFirstByProductAndValidUntilIsNull(Product product);
}
