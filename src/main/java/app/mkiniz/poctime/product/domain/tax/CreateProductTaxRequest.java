package app.mkiniz.poctime.product.domain.tax;

import app.mkiniz.poctime.product.ProductConstants;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateProductTaxRequest(
        Tsid id,
        @NotNull(message = ProductConstants.PRODUCT_NOT_NULL)
        Tsid productId,
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
        GoodsOrigin origin,
        LocalDate validFrom,
        LocalDate validUntil
) {
    public static CreateProductTaxRequest from(ProductTaxData taxData) {
        if (taxData == null) {
            return null;
        }
        return CreateProductTaxRequest.builder()
                .id(taxData.getId() != null ? Tsid.from(taxData.getId()) : null)
                .productId(Tsid.from(taxData.getProduct().getId()))
                .ncm(taxData.getNcm())
                .cstIpi(taxData.getCstIpi())
                .cstPis(taxData.getCstPis())
                .cstCofins(taxData.getCstCofins())
                .cfop(taxData.getCfop())
                .productType(taxData.getProductType())
                .origin(taxData.getOrigin())
                .validFrom(taxData.getValidFrom())
                .validUntil(taxData.getValidUntil())
                .build();
    }
}
