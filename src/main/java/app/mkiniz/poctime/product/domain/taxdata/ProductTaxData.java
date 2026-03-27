package app.mkiniz.poctime.product.domain.taxdata;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "product_tax_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTaxData {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tax_data_product"))
    private Product product;

    @Column(name = "ncm", length = 8)
    private String ncm;

    @Column(name = "cest", length = 7)
    private String cest;

    @Column(name = "cfop", length = 4)
    private String cfop;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin")
    private GoodsOrigin origin;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    public boolean isBefore(LocalDate date) {
        return Objects.nonNull(validUntil) && date.isBefore(validUntil);
    }

    public boolean isAfter(LocalDate date) {
        return Objects.isNull(validUntil) && !date.isAfter(validUntil);
    }

    public Either<BusinessException, ProductTaxData> valid() {
        if (Objects.isNull(product))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_NULL));
        if (StringUtils.isBlank(ncm))
            return Either.left(new BusinessException(ProductConstants.NCM_NOT_BLANK));
        if (StringUtils.isBlank(cest))
            return Either.left(new BusinessException(ProductConstants.CST_NOT_BLANK));
        if (StringUtils.isBlank(cfop))
            return Either.left(new BusinessException(ProductConstants.CFOP_NOT_BLANK));
        
        return Either.right(this);
    }

}
