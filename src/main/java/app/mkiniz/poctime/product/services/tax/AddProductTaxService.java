package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.tax.CreateProductTaxRequest;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.product.services.ServiceDefaults;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class AddProductTaxService
        extends AbstractTaxProcessService
        implements AddBusinessUseCase<CreateProductTaxRequest, ProductTaxResponse>, ServiceDefaults {

    private final ProductTaxDataRepository productTaxDataRepository;
    private final ProductRepository productRepository;
    private final TsidGenerator tsidGenerator;

    public AddProductTaxService(
            NCMService ncmService,
            CSTRepository csvRepository,
            CFOPRepository cfopRepository,
            HistoryService historyService,
            ProductTaxDataRepository productTaxDataRepository,
            ProductRepository productRepository,
            TsidGenerator tsidGenerator) {
        super(ncmService, csvRepository, cfopRepository, historyService);
        this.productTaxDataRepository = productTaxDataRepository;
        this.productRepository = productRepository;
        this.tsidGenerator = tsidGenerator;
    }

    @Override
    public ProductTaxResponse execute(CreateProductTaxRequest request) {
        return (ProductTaxResponse) createContext(request)
                .flatMap(this::findProduct)
                .flatMap(this::createTaxData)
                .flatMap(this::validateTaxBusiness)
                .flatMap(this::saveTaxData)
                .map(context -> ProductTaxResponse.from(context.taxData))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> validateTaxBusiness(Context context) {
        return context.taxData.valid()
                .map(taxData -> context)
                .flatMap(this::validateNcm)
                .flatMap(this::validateCst)
                .flatMap(this::validateCfop)
                .flatMap(this::addHistory);
    }

    private Either<BusinessException, Context> createContext(CreateProductTaxRequest request) {
        if (Objects.isNull(request))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_REQUEST_NOT_NULL));
        return Either.right(Context.of(request));
    }

    private Either<BusinessException, Context> findProduct(Context context) {
        if (Objects.isNull(context.request.productId())) {
            return Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND));
        }
        Optional<Product> product = productRepository.findById(context.request.productId().toLong());
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
                .build();
        return Either.right(context);
    }

    private Either<BusinessException, Context> saveTaxData(Context context) {
        if (context.historyAdded.hasAdjustedEntity())
            productTaxDataRepository.save((ProductTaxData) context.historyAdded.adjustedEntity());
        productTaxDataRepository.save(context.taxData);
        return Either.right(context);
    }

}
