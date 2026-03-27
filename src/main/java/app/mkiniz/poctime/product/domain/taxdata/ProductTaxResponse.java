package app.mkiniz.poctime.product.domain.taxdata;

import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ProductTaxResponse(
        Tsid id,
        String ncm,
        String cest,
        String cfop,
        ProductType productType,
        GoodsOrigin origin,
        LocalDate validFrom,
        LocalDate validUntil
) {
    public static ProductTaxResponse from(ProductTaxData taxData) {
        if (taxData == null) {
            return null;
        }
        return ProductTaxResponse.builder()
                .id(taxData.getId() != null ? Tsid.from(taxData.getId()) : null)
                .ncm(taxData.getNcm())
                .cest(taxData.getCest())
                .cfop(taxData.getCfop())
                .productType(taxData.getProductType())
                .origin(taxData.getOrigin())
                .validFrom(taxData.getValidFrom())
                .validUntil(taxData.getValidUntil())
                .build();
    }
}
