package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.product.domain.tax.UpdateProductTaxRequest;
import app.mkiniz.poctime.product.services.ServiceDefaults;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UpdateProductTaxService implements UpdateBusinessUseCase<Tsid, UpdateProductTaxRequest, ProductTaxResponse>, ServiceDefaults {

    private final ProductTaxDataRepository productTaxDataRepository;
    private final TsidGenerator tsidGenerator;
    private final NCMService ncmService;
    private final CSTRepository csvRepository;
    private final CFOPRepository cfopRepository;

    @Override
    public ProductTaxResponse execute(Tsid id, UpdateProductTaxRequest request) {
        return (ProductTaxResponse) createContext(request)
                .flatMap(this::findProductTax)
                .flatMap(this::updateTaxData)
                .flatMap(this::validateTaxBusiness)
                .flatMap(this::saveTaxData)
                .map(context -> ProductTaxResponse.from(context.taxData))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> validateTaxBusiness(Context context) {
        return context.taxData.valid()
                .map(taxData -> context)
                .flatMap(ctx -> {
                    if (ncmService.findByCode(ctx.request.ncm()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NCM_NOT_FOUND));
                    return Either.right(ctx);
                })
                .flatMap(ctx -> {
                    if (csvRepository.findIpiByCode(ctx.request.cstIpi()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_IPI_NOT_FOUND));

                    if (csvRepository.findPisByCode(ctx.request.cstPis()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_PIS_NOT_FOUND));

                    if (csvRepository.findCofinsByCode(ctx.request.cstCofins()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_COFINS_NOT_FOUND));
                    return Either.right(ctx);
                })
                .flatMap(ctx -> {
                    if (cfopRepository.findByCode(ctx.request.cfop()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CFOP_NOT_FOUND));
                    return Either.right(ctx);
                })
                .flatMap(ctx -> {
                    List<ProductTaxData> taxes = context.taxData
                            .getProduct()
                            .getTaxDataHistory()
                            .stream()
                            .sorted(Comparator.comparing(ProductTaxData::getId))
                            .toList();
                    int position = taxes.indexOf(ctx.taxData);
                    context.previousTax = position > 0 ? Optional.of(taxes.get(position - 1)) : Optional.empty();
                    context.nextTax = taxes.size() <= position + 1 ? Optional.of(taxes.get(position + 1)) : Optional.empty();
                    context.previousTax.ifPresent(productTaxData -> productTaxData.setValidUntil(ctx.request.validFrom().minusDays(1)));
                    if (context.nextTax.isPresent()) {
                        context.nextTax.get().setValidFrom(ctx.request.validFrom().plusDays(1));
                    }

                    return Either.right(ctx);
                });
    }

    private Either<BusinessException, Context> createContext(UpdateProductTaxRequest request) {
        if (Objects.isNull(request))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_REQUEST_NOT_NULL));
        return Either.right(Context.of(request));
    }

    private Either<BusinessException, Context> findProductTax(Context context) {
        if (Objects.isNull(context.request.id())) {
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NOT_FOUND));
        }
        Optional<ProductTaxData> productTaxData = productTaxDataRepository.findById(context.request.id().toLong());
        productTaxData.ifPresent(value -> context.taxData = value);
        return productTaxData.isPresent() ?
                Either.right(context) :
                Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND));
    }

    private Either<BusinessException, Context> updateTaxData(Context context) {
        ProductTaxData taxData = context.taxData;
        taxData.setNcm(context.request.ncm());
        taxData.setCstIpi(context.request.cstIpi());
        taxData.setCstPis(context.request.cstPis());
        taxData.setCstCofins(context.request.cstCofins());
        taxData.setCfop(context.request.cfop());
        taxData.setProductType(context.request.productType());
        taxData.setOrigin(context.request.origin());
        taxData.setValidFrom(context.request.validFrom());
        taxData.setValidUntil(context.request.validUntil());
        return Either.right(context);
    }

    private Either<BusinessException, Context> saveTaxData(Context context) {
        if (Objects.nonNull(context.previousTax)) {
            //context.previousTax.setValidUntil(context.request.validFrom().minusDays(1));
            //context.previousTax = productTaxDataRepository.save(context.previousTax);
        }
        context.taxData = productTaxDataRepository.save(context.taxData);
        return Either.right(context);
    }

    private static class Context {
        public final UpdateProductTaxRequest request;
        public ProductTaxData taxData;
        public Optional<ProductTaxData> previousTax;
        public Optional<ProductTaxData> nextTax;

        private Context(UpdateProductTaxRequest request) {
            this.request = request;
            this.previousTax = Optional.empty();
            this.nextTax = Optional.empty();
        }

        public static Context of(UpdateProductTaxRequest request) {
            return new Context(request);
        }
    }
}
