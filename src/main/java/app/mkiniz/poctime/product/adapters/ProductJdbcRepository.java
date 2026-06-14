package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.ProductSearchRequest;
import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.tax.GoodsOrigin;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductJdbcRepository implements ProductRepository {

    private final JdbcClient jdbcClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Optional<Product> findById(Long id) {
        Optional<Product> productOpt = jdbcClient.sql("SELECT p.*, c.name as category_name FROM product p LEFT JOIN category c ON p.category_id = c.id WHERE p.id = :id AND p.deleted = false")
                .param("id", id)
                .query((rs, rowNum) -> Product.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .sku(rs.getString("sku"))
                        .deleted(rs.getBoolean("deleted"))
                        .category(rs.getObject("category_id") != null ? Category.builder()
                                .id(rs.getLong("category_id"))
                                .name(rs.getString("category_name"))
                                .build() : null)
                        .build())
                .optional();

        productOpt.ifPresent(this::loadTaxDataHistory);
        return productOpt;
    }

    private void loadTaxDataHistory(Product product) {
        List<ProductTaxData> taxHistory = jdbcClient.sql("SELECT * FROM product_tax_data WHERE product_id = :productId AND deleted = false ORDER BY valid_from DESC")
                .param("productId", product.getId())
                .query((rs, rowNum) -> ProductTaxData.builder()
                        .id(rs.getLong("id"))
                        .product(product)
                        .ncm(rs.getString("ncm"))
                        .cstIpi(rs.getString("cst_ipi"))
                        .cstPis(rs.getString("cst_pis"))
                        .cstCofins(rs.getString("cst_cofins"))
                        .cfop(rs.getString("cfop"))
                        .productType(ProductType.valueOf(rs.getString("product_type")))
                        .origin(GoodsOrigin.valueOf(rs.getString("origin")))
                        .validFrom(rs.getDate("valid_from").toLocalDate())
                        .validUntil(rs.getDate("valid_until") != null ? rs.getDate("valid_until").toLocalDate() : null)
                        .deleted(rs.getBoolean("deleted"))
                        .build())
                .list();
        product.setTaxDataHistory(taxHistory);
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }

        int updated = jdbcClient.sql("UPDATE product SET name = :name, description = :description, sku = :sku, category_id = :categoryId, deleted = :deleted WHERE id = :id")
                .param("id", product.getId())
                .param("name", product.getName())
                .param("description", product.getDescription())
                .param("sku", product.getSku())
                .param("categoryId", product.getCategory() != null ? product.getCategory().getId() : null)
                .param("deleted", product.isDeleted())
                .update();

        if (updated == 0) {
            jdbcClient.sql("INSERT INTO product (id, name, description, sku, category_id, deleted) VALUES (:id, :name, :description, :sku, :categoryId, :deleted)")
                    .param("id", product.getId())
                    .param("name", product.getName())
                    .param("description", product.getDescription())
                    .param("sku", product.getSku())
                    .param("categoryId", product.getCategory() != null ? product.getCategory().getId() : null)
                    .param("deleted", product.isDeleted())
                    .update();
        }

        product.domainEvents().forEach(eventPublisher::publishEvent);
        product.clearDomainEvents();

        return product;
    }

    @Override
    public void deleteById(Long id) {
        jdbcClient.sql("UPDATE product SET deleted = true WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public Page<Product> findAll(ProductSearchRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT p.*, c.name as category_name FROM product p LEFT JOIN category c ON p.category_id = c.id WHERE p.deleted = false");
        List<Object> params = new ArrayList<>();

        if (request.getName() != null && !request.getName().isBlank()) {
            sql.append(" AND p.name ILIKE ?");
            params.add("%" + request.getName() + "%");
        }
        if (request.getSku() != null && !request.getSku().isBlank()) {
            sql.append(" AND p.sku ILIKE ?");
            params.add("%" + request.getSku() + "%");
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            sql.append(" AND c.name ILIKE ?");
            params.add("%" + request.getCategory() + "%");
        }

        String countSql = "SELECT count(*) FROM (" + sql + ") as total";
        Long total = jdbcClient.sql(countSql).params(params).query(Long.class).single();

        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<Product> products = jdbcClient.sql(sql.toString())
                .params(params)
                .query((rs, rowNum) -> Product.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .sku(rs.getString("sku"))
                        .deleted(rs.getBoolean("deleted"))
                        .category(rs.getObject("category_id") != null ? Category.builder()
                                .id(rs.getLong("category_id"))
                                .name(rs.getString("category_name"))
                                .build() : null)
                        .build())
                .list();

        products.forEach(this::loadTaxDataHistory);

        return new PageImpl<>(products, pageable, total);
    }
}
