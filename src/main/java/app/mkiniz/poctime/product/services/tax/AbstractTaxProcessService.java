package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractTaxProcessService {

    protected final NCMService ncmService;
    protected final CSTRepository csvRepository;
    protected final CFOPRepository cfopRepository;
    protected final HistoryService historyService;

    protected Either<BusinessException, ? extends ContextRequest> validateNcm(ContextRequest ctx) {
        if (ncmService.findByCode(ctx.ncm()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_NCM_NOT_FOUND));
        return Either.right(ctx);
    }

    protected Either<BusinessException, ? extends ContextRequest> validateCst(ContextRequest ctx) {
        if (csvRepository.findIpiByCode(ctx.cstIpi()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_IPI_NOT_FOUND));

        if (csvRepository.findPisByCode(ctx.cstPis()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_PIS_NOT_FOUND));

        if (csvRepository.findCofinsByCode(ctx.cstCofins()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CST_COFINS_NOT_FOUND));
        return Either.right(ctx);
    }

    protected Either<BusinessException, ? extends ContextRequest> validateCfop(ContextRequest ctx) {
        if (cfopRepository.findByCode(ctx.cfop()).isEmpty())
            return Either.left(new BusinessException(ProductConstants.PRODUCT_TAX_CFOP_NOT_FOUND));
        return Either.right(ctx);
    }

    protected static interface ContextRequest {
        String ncm();

        String cstIpi();

        String cstPis();

        String cstCofins();

        String cfop();
    }
}
