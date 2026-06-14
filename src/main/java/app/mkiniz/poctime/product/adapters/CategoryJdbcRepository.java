package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryJdbcRepository implements CategoryRepository {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<Category> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM category WHERE id = :id AND deleted = false")
                .param("id", id)
                .query(Category.class)
                .optional();
    }

    @Override
    public List<Category> findAll() {
        return jdbcClient.sql("SELECT * FROM category WHERE deleted = false")
                .query(Category.class)
                .list();
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }

        int updated = jdbcClient.sql("UPDATE category SET name = :name, deleted = :deleted WHERE id = :id")
                .param("id", category.getId())
                .param("name", category.getName())
                .param("deleted", category.isDeleted())
                .update();

        if (updated == 0) {
            jdbcClient.sql("INSERT INTO category (id, name, deleted) VALUES (:id, :name, :deleted)")
                    .param("id", category.getId())
                    .param("name", category.getName())
                    .param("deleted", category.isDeleted())
                    .update();
        }

        return category;
    }

    @Override
    public void deleteById(Long id) {
        jdbcClient.sql("UPDATE category SET deleted = true WHERE id = :id")
                .param("id", id)
                .update();
    }
}
