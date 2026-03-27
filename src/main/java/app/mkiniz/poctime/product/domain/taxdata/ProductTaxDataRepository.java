package app.mkiniz.poctime.product.domain.taxdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTaxDataRepository extends JpaRepository<ProductTaxData, Long>, JpaSpecificationExecutor<ProductTaxData> {
}
