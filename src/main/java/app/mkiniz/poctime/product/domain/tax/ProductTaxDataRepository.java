package app.mkiniz.poctime.product.domain.tax;

import app.mkiniz.poctime.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTaxDataRepository extends JpaRepository<ProductTaxData, Long>, JpaSpecificationExecutor<ProductTaxData> {

    Optional<ProductTaxData> findFirstByProductAndValidUntilIsNull(Product product);

}
