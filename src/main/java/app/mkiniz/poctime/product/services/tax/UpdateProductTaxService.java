package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.product.domain.tax.UpdateProductTaxRequest;
import app.mkiniz.poctime.product.services.ServiceDefaults;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UpdateProductTaxService
        extends AbstractTaxProcessService
        implements UpdateBusinessUseCase<Tsid, UpdateProductTaxRequest, ProductTaxResponse>, ServiceDefaults {

    private final ProductTaxDataRepository productTaxDataRepository;

    public UpdateProductTaxService(
            NCMService ncmService,
            CSTRepository csvRepository,
            CFOPRepository cfopRepository,
            HistoryService historyService,
            ProductTaxDataRepository productTaxDataRepository) {
        super(ncmService, csvRepository, cfopRepository, historyService);
        this.productTaxDataRepository = productTaxDataRepository;
    }

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
                .flatMap(this::validateNcm)
                .flatMap(this::validateCst)
                .flatMap(this::validateCfop)
                .map(requestCtx -> context);
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
        return Either.right(context);
    }

    private Either<BusinessException, Context> saveTaxData(Context context) {
        context.taxData = productTaxDataRepository.save(context.taxData);
        return Either.right(context);
    }


    private static class Context implements ContextRequest {
        public final UpdateProductTaxRequest request;
        public ProductTaxData taxData;

        protected Context(UpdateProductTaxRequest request) {
            this.request = request;
        }

        public static Context of(UpdateProductTaxRequest request) {
            return new Context(request);
        }

        @Override
        public String ncm() {
            return this.request.ncm();
        }

        @Override
        public String cstIpi() {
            return this.request.cstIpi();
        }

        @Override
        public String cstPis() {
            return this.request.cstPis();
        }

        @Override
        public String cstCofins() {
            return this.request.cstCofins();
        }

        @Override
        public String cfop() {
            return this.request.cfop();
        }
    }

}
