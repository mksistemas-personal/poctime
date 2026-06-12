package app.mkiniz.poctime.product.services.base;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.GetByIdBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetProductByIdService implements GetByIdBusinessUseCase<Tsid, ProductResponse> {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse execute(Tsid id) {
        return (ProductResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findProduct)
                .fold(this::throwBusinessException, ProductResponse::from);
    }

    private Either<? extends BusinessException, ? extends Product> findProduct(Tsid productId) {
        return productRepository.findById(productId.toLong())
                .<Either<BusinessException, Product>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND)));
    }
}
