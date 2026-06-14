package app.mkiniz.poctime.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long id);

    Page<Product> findAll(ProductSearchRequest request, Pageable pageable);
}
