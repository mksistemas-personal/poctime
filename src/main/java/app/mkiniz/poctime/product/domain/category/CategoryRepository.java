package app.mkiniz.poctime.product.domain.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(Long id);

    List<Category> findAll();

    Category save(Category category);

    void deleteById(Long id);
}
