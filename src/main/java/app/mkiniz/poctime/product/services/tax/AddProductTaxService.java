package app.mkiniz.poctime.product.services.tax;

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

    @Override
    public ProductTaxResponse execute(ProductTaxRequest request) {
        return (ProductTaxResponse) createContext(request)
                .flatMap(this::findProduct)
                .flatMap(this::createTaxData)
                .flatMap(this::saveTaxData)
                .map(context -> ProductTaxResponse.from(context.taxData))
                .fold(this::throwBusinessException, response -> response);
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
                .cest(context.request.cest())
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

        private Context(ProductTaxRequest request) {
            this.request = request;
        }

        public static Context of(ProductTaxRequest request) {
            return new Context(request);
        }
    }
}
