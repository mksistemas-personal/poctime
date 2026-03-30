package app.mkiniz.poctime.product.domain.taxdata;

import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ProductTaxEvent(
        Tsid id,
        String ncm,
        String cstIpi,
        String cstPis,
        String cstCofins,
        String cfop,
        ProductType productType,
        GoodsOrigin origin,
        LocalDate validFrom,
        LocalDate validUntil
) {
    public static ProductTaxEvent from(ProductTaxData taxData) {
        if (taxData == null) {
            return null;
        }
        return ProductTaxEvent.builder()
                .id(taxData.getId() != null ? Tsid.from(taxData.getId()) : null)
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
