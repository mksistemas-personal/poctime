package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryErrorEnum;
import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.tax.CreateProductTaxRequest;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractTaxProcessService {

    protected final NCMService ncmService;
    protected final CSTRepository csvRepository;
    protected final CFOPRepository cfopRepository;
    protected final HistoryService historyService;

    protected Either<BusinessException, Context> validateNcm(Context ctx) {
        if (ncmService.findByCode(ctx.request.ncm()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NCM_NOT_FOUND));
        return Either.right(ctx);
    }

    protected Either<BusinessException, Context> validateCst(Context ctx) {
        if (csvRepository.findIpiByCode(ctx.request.cstIpi()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_IPI_NOT_FOUND));

        if (csvRepository.findPisByCode(ctx.request.cstPis()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_PIS_NOT_FOUND));

        if (csvRepository.findCofinsByCode(ctx.request.cstCofins()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_COFINS_NOT_FOUND));
        return Either.right(ctx);
    }

    protected Either<BusinessException, Context> validateCfop(Context ctx) {
        if (cfopRepository.findByCode(ctx.request.cfop()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CFOP_NOT_FOUND));
        return Either.right(ctx);
    }

    protected Either<BusinessException, Context> addHistory(Context ctx) {
        return historyService.addHistory(ctx.taxData, (error, history) -> {
                    return switch (error) {
                        case HistoryErrorEnum.VALID_FROM_NULL ->
                                new BusinessException(ProductConstants.PRODUCT_TAX_VALID_FROM_NOT_NULL);
                        case HistoryErrorEnum.VALID_UNTIL_NOT_NULL ->
                                new BusinessException(ProductConstants.PRODUCT_TAX_VALID_UNTIL_MOST_BE_NULL);
                        case HistoryErrorEnum.VALID_FROM_SMALLER_THEN_LAST_VALID_FROM_HISTORY ->
                                new BusinessException(ProductConstants.PRODUCT_TAX_VALID_FROM_SMALLER_THAN_LAST_VALUE);
                        default -> new BusinessException(error.name());
                    };
                })
                .map(response -> {
                    ctx.historyAdded = response;
                    return ctx;
                });
    }

    protected static class Context {
        public final CreateProductTaxRequest request;
        public Product product;
        public ProductTaxData taxData;
        public HistoryService.HistoryAdded historyAdded;

        protected Context(CreateProductTaxRequest request) {
            this.request = request;
        }

        public static Context of(CreateProductTaxRequest request) {
            return new Context(request);
        }
    }
}
