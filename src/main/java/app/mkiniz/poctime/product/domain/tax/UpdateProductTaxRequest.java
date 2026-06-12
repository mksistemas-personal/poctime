package app.mkiniz.poctime.product.domain.tax;

import app.mkiniz.poctime.product.ProductConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateProductTaxRequest(
        @NotBlank(message = ProductConstants.PRODUCT_TAX_NCM_NOT_BLANK)
        String ncm,
        @NotBlank(message = ProductConstants.PRODUCT_TAX_CST_IPI_NOT_BLANK)
        String cstIpi,
        @NotBlank(message = ProductConstants.PRODUCT_TAX_CST_PIS_NOT_BLANK)
        String cstPis,
        @NotBlank(message = ProductConstants.PRODUCT_TAX_CST_COFINS_NOT_BLANK)
        String cstCofins,
        @NotBlank(message = ProductConstants.PRODUCT_TAX_CFOP_NOT_BLANK)
        String cfop,
        ProductType productType,
        GoodsOrigin origin
) {
}
