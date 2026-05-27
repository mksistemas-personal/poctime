package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryEntity;
import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.product.services.ServiceDefaults;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class DeleteProductTaxService
        implements DeleteBusinessUseCase<Tsid, ProductTaxResponse>, ServiceDefaults {

    private final ProductTaxDataRepository productTaxDataRepository;
    private final HistoryService historyService;

    public DeleteProductTaxService(
            HistoryService historyService,
            ProductTaxDataRepository productTaxDataRepository) {
        this.productTaxDataRepository = productTaxDataRepository;
        this.historyService = historyService;
    }

    @Override
    public ProductTaxResponse execute(Tsid id) {
        return (ProductTaxResponse) createContext(id)
                .flatMap(this::findProductTax)
                .flatMap(this::deleteTaxData)
                .map(context -> ProductTaxResponse.from(context.taxData))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> createContext(Tsid id) {
        if (Objects.isNull(id))
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NOT_FOUND));
        return Either.right(Context.of(id));
    }

    private Either<BusinessException, Context> findProductTax(Context context) {
        Optional<ProductTaxData> productTaxData = productTaxDataRepository.findById(context.id.toLong());
        productTaxData.ifPresent(value -> context.taxData = value);
        return productTaxData.isPresent() ?
                Either.right(context) :
                Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NOT_FOUND));
    }

    private Either<BusinessException, Context> deleteTaxData(Context context) {
        Optional<HistoryEntity> toAdjust = historyService.adjustFromDeletedHistory(context.taxData);
        productTaxDataRepository.delete(context.taxData);
        toAdjust.ifPresent(entity -> productTaxDataRepository.save((ProductTaxData) toAdjust.get()));
        return Either.right(context);
    }

    private static class Context {
        public ProductTaxData taxData;
        public Tsid id;

        protected Context(Tsid id) {
            this.id = id;
        }

        public static Context of(Tsid id) {
            return new Context(id);
        }
    }
}
