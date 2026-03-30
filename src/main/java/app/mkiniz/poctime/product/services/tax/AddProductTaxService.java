package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxData;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxRequest;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxResponse;
import app.mkiniz.poctime.product.services.ServiceDefaults;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class AddProductTaxService implements AddBusinessUseCase<ProductTaxRequest, ProductTaxResponse>, ServiceDefaults {

    private final ProductTaxDataRepository productTaxDataRepository;
    private final ProductRepository productRepository;
    private final TsidGenerator tsidGenerator;
    private final NCMService ncmService;
    private final CSTRepository csvRepository;
    private final CFOPRepository cfopRepository;

    @Override
    public ProductTaxResponse execute(ProductTaxRequest request) {
        return (ProductTaxResponse) createContext(request)
                .flatMap(this::findProduct)
                .flatMap(this::closePreviousTaxData)
                .flatMap(this::createTaxData)
                .flatMap(this::validateTaxBusiness)
                .flatMap(this::saveTaxData)
                .map(context -> ProductTaxResponse.from(context.taxData))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> closePreviousTaxData(Context context) {
        productTaxDataRepository.findFirstByProductAndValidUntilIsNull(context.product)
                .ifPresent(oldTaxData -> {
                    oldTaxData.setValidUntil(context.request.validFrom());
                    productTaxDataRepository.save(oldTaxData);
                });
        return Either.right(context);
    }

    private Either<BusinessException, Context> validateTaxBusiness(Context context) {
        return context.taxData.valid()
                .map(taxData -> context)
                .flatMap(ctx -> {
                    if (ncmService.findByCode(ctx.request.ncm()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.NCM_NOT_FOUND));
                    return Either.right(ctx);
                })
                .flatMap(ctx -> {
                    if (csvRepository.findIpiByCode(ctx.request.cstIpi()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.CST_IPI_NOT_FOUND));

                    if (csvRepository.findPisByCode(ctx.request.cstPis()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.CST_PIS_NOT_FOUND));

                    if (csvRepository.findCofinsByCode(ctx.request.cstCofins()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.CST_COFINS_NOT_FOUND));
                    return Either.right(ctx);
                })
                .flatMap(ctx -> {
                    if (cfopRepository.findByCode(ctx.request.cfop()).isEmpty())
                        return Either.left(new BusinessException(ProductConstants.CFOP_NOT_FOUND));
                    return Either.right(ctx);
                })
                .flatMap(ctx -> {
                    Optional<ProductTaxData> previousTax = productTaxDataRepository.findFirstByProductAndValidUntilIsNull(ctx.product);
                    if (previousTax.isPresent()) {
                        ProductTaxData value = previousTax.get();
                        context.previousTax = value;

                    }
                    return Either.right(ctx);
                });
    }

    private Either<BusinessException, Context> createContext(ProductTaxRequest request) {
        if (Objects.isNull(request))
            return Either.left(new BusinessException(ProductConstants.REQUEST_TAX_NOT_NULL));
        return Either.right(Context.of(request));
    }

    private Either<BusinessException, Context> findProduct(Context context) {
        if (Objects.isNull(context.request.id())) {
            return Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND));
        }
        Optional<Product> product = productRepository.findById(context.request.id().toLong());
        product.ifPresent(value -> context.product = value);
        return product.isPresent() ?
                Either.right(context) :
                Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND));
    }

    private Either<BusinessException, Context> createTaxData(Context context) {
        context.taxData = ProductTaxData.builder()
                .id(tsidGenerator.newIdAsLong())
                .product(context.product)
                .ncm(context.request.ncm())
                .cstIpi(context.request.cstIpi())
                .cstPis(context.request.cstPis())
                .cstCofins(context.request.cstCofins())
                .cfop(context.request.cfop())
                .productType(context.request.productType())
                .origin(context.request.origin())
                .validFrom(context.request.validFrom())
                .validUntil(context.request.validUntil())
                .build();
        return Either.right(context);
    }

    private Either<BusinessException, Context> saveTaxData(Context context) {
        context.taxData = productTaxDataRepository.save(context.taxData);
        // Opcional: registrar evento no produto se necessário, mas ProductTaxData parece ser uma entidade independente aqui.
        // Se quisermos disparar o evento de atualização do produto:
        // context.product.updated();
        // productRepository.save(context.product);
        return Either.right(context);
    }

    private static class Context {
        public final ProductTaxRequest request;
        public Product product;
        public ProductTaxData taxData;
        public ProductTaxData previousTax;

        private Context(ProductTaxRequest request) {
            this.request = request;
        }

        public static Context of(ProductTaxRequest request) {
            return new Context(request);
        }
    }
}
