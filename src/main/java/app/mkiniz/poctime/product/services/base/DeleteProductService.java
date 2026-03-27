package app.mkiniz.poctime.product.services.base;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class DeleteProductService implements DeleteBusinessUseCase<Tsid, ProductResponse> {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse execute(Tsid id) {
        return (ProductResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findProduct)
                .flatMap(this::deleteProduct)
                .map(ProductResponse::from)
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<? extends BusinessException, ? extends Product> deleteProduct(Product product) {
        product.deleted();
        productRepository.delete(product);
        return Either.right(product);
    }

    private Either<? extends BusinessException, ? extends Product> findProduct(Tsid productId) {
        Optional<Product> product = productRepository.findById(productId.toLong());
        return product.<Either<? extends BusinessException, ? extends Product>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND)));
    }
}
