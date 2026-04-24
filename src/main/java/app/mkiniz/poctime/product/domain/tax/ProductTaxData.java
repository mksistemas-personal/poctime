package app.mkiniz.poctime.product.domain.tax;

import app.mkiniz.poctime.base.historic.HistoryEntity;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Table(name = "product_tax_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE product SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Audited(targetAuditMode = NOT_AUDITED)
public class ProductTaxData implements HistoryEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tax_data_product"))
    private Product product;

    @Column(name = "ncm", length = 10, nullable = false)
    private String ncm;

    @Column(name = "cst_ipi", length = 2, nullable = false)
    private String cstIpi;

    @Column(name = "cst_pis", length = 2, nullable = false)
    private String cstPis;

    @Column(name = "cst_cofins", length = 2, nullable = false)
    private String cstCofins;

    @Column(name = "cfop", length = 4, nullable = false)
    private String cfop;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false)
    private GoodsOrigin origin;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    public boolean isBefore(LocalDate date) {
        return Objects.nonNull(validUntil) && date.isBefore(validUntil);
    }

    public boolean isAfter(LocalDate date) {
        return Objects.isNull(validUntil) && !date.isAfter(validUntil);
    }

    public Either<BusinessException, ProductTaxData> valid(boolean isInsert) {
        if (Objects.isNull(product))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_NULL));
        if (StringUtils.isBlank(ncm))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NCM_NOT_BLANK));
        if (StringUtils.isBlank(cstIpi))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_IPI_NOT_BLANK));
        if (StringUtils.isBlank(cstPis))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_PIS_NOT_BLANK));
        if (StringUtils.isBlank(cstCofins))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_COFINS_NOT_BLANK));
        if (StringUtils.isBlank(cfop))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CFOP_NOT_BLANK));
        if (Objects.isNull(validFrom))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_VALID_FROM_NOT_NULL));
        if (isInsert && Objects.nonNull(validUntil))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_VALID_UNTIL_MOST_BE_NULL));
        return Either.right(this);
    }

    public boolean validFromAndUntil(ProductTaxData previousTax) {
        if (Objects.nonNull(previousTax)) {
            return validFrom.isAfter(previousTax.validFrom);
        }
        return true;
    }

    @Override
    public LocalDate validFrom() {
        return this.validFrom;
    }

    @Override
    public LocalDate validUntil() {
        return this.validUntil;
    }

    @Override
    public void validFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public void validUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public List<HistoryEntity> getHistory() {
        List<ProductTaxData> list = this.getProduct().getTaxDataHistory();
        return Objects.isNull(list) ? List.of() : List.copyOf(list);
    }
}
