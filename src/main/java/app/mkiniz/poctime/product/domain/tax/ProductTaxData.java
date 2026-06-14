package app.mkiniz.poctime.product.domain.tax;

import app.mkiniz.poctime.base.historic.HistoryEntity;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTaxData implements HistoryEntity {

    private Long id;

    private Product product;

    private String ncm;

    private String cstIpi;

    private String cstPis;

    private String cstCofins;

    private String cfop;

    private ProductType productType;

    private GoodsOrigin origin;

    private LocalDate validFrom;

    private LocalDate validUntil;

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
