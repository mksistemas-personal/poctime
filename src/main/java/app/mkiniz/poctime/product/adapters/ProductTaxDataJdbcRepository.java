package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.tax.GoodsOrigin;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductTaxDataJdbcRepository implements ProductTaxDataRepository {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<ProductTaxData> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM product_tax_data WHERE id = :id AND deleted = false")
                .param("id", id)
                .query((rs, rowNum) -> ProductTaxData.builder()
                        .id(rs.getLong("id"))
                        .product(Product.builder().id(rs.getLong("product_id")).build())
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
                .optional();
    }

    @Override
    public ProductTaxData save(ProductTaxData data) {
        if (data.getId() == null) {
            throw new IllegalArgumentException("ProductTaxData id cannot be null");
        }

        int updated = jdbcClient.sql("""
                        UPDATE product_tax_data 
                        SET product_id = :productId, ncm = :ncm, cst_ipi = :cstIpi, cst_pis = :cstPis, 
                            cst_cofins = :cstCofins, cfop = :cfop, product_type = :productType, 
                            origin = :origin, valid_from = :validFrom, valid_until = :validUntil, deleted = :deleted 
                        WHERE id = :id
                        """)
                .param("id", data.getId())
                .param("productId", data.getProduct().getId())
                .param("ncm", data.getNcm())
                .param("cstIpi", data.getCstIpi())
                .param("cstPis", data.getCstPis())
                .param("cstCofins", data.getCstCofins())
                .param("cfop", data.getCfop())
                .param("productType", data.getProductType().name())
                .param("origin", data.getOrigin().name())
                .param("validFrom", data.getValidFrom())
                .param("validUntil", data.getValidUntil())
                .param("deleted", data.isDeleted())
                .update();

        if (updated == 0) {
            jdbcClient.sql("""
                            INSERT INTO product_tax_data (id, product_id, ncm, cst_ipi, cst_pis, cst_cofins, cfop, product_type, origin, valid_from, valid_until, deleted) 
                            VALUES (:id, :productId, :ncm, :cstIpi, :cstPis, :cstCofins, :cfop, :productType, :origin, :validFrom, :validUntil, :deleted)
                            """)
                    .param("id", data.getId())
                    .param("productId", data.getProduct().getId())
                    .param("ncm", data.getNcm())
                    .param("cstIpi", data.getCstIpi())
                    .param("cstPis", data.getCstPis())
                    .param("cstCofins", data.getCstCofins())
                    .param("cfop", data.getCfop())
                    .param("productType", data.getProductType().name())
                    .param("origin", data.getOrigin().name())
                    .param("validFrom", data.getValidFrom())
                    .param("validUntil", data.getValidUntil())
                    .param("deleted", data.isDeleted())
                    .update();
        }

        return data;
    }

    @Override
    public void deleteById(Long id) {
        jdbcClient.sql("UPDATE product_tax_data SET deleted = true WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public Optional<ProductTaxData> findFirstByProductAndValidUntilIsNull(Product product) {
        return jdbcClient.sql("SELECT * FROM product_tax_data WHERE product_id = :productId AND valid_until IS NULL AND deleted = false")
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
                        .validUntil(null)
                        .deleted(rs.getBoolean("deleted"))
                        .build())
                .optional();
    }
}
