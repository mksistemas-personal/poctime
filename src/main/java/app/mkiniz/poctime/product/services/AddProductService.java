package app.mkiniz.poctime.product.services;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.*;
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
class AddProductService implements AddBusinessUseCase<CreateProductRequest, ProductResponse> {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TsidGenerator tsidGenerator;

    @Override
    public ProductResponse execute(CreateProductRequest request) {
        return (ProductResponse) createContext(request)
                .flatMap(this::findCategory)
                .flatMap(this::createProduct)
                .flatMap(this::saveProduct)
                .map(context -> ProductResponse.from(context.product))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> createContext(CreateProductRequest request) {
        return Either.right(Context.of(request));
    }

    private Either<BusinessException, Context> findCategory(Context context) {
        if (context.request.category() == null) {
            return Either.left(new BusinessException(ProductConstants.CATEGORY_NOT_FOUND));
        }
        if (context.isNewCategory()) {
            context.category = categoryRepository.save(
                    Category.builder()
                            .id(tsidGenerator.newIdAsLong())
                            .name(context.request.category().name())
                            .build());
            return Either.right(context);
        } else {
            Optional<Category> category = categoryRepository.findById(context.getCategoryRequestId());
            category.ifPresent(value -> context.category = value);
            return category.isPresent() ?
                    Either.right(context) :
                    Either.left(new BusinessException(ProductConstants.CATEGORY_NOT_FOUND));
        }
    }

    private Either<BusinessException, Context> createProduct(Context context) {
        context.product = Product.builder()
                .id(new TsidGenerator().newIdAsLong())
                .name(context.request.name())
                .category(context.category)
                .sku(context.request.sku())
                .description(context.request.description())
                .build();
        return Either.right(context);
    }

    private Either<BusinessException, Context> saveProduct(Context context) {
        context.product.created();
        context.product = productRepository.save(context.product);
        return Either.right(context);
    }

    private static class Context {
        public final CreateProductRequest request;
        public Category category;
        public Product product;

        private Context(CreateProductRequest request) {
            this.request = request;
        }

        public static Context of(CreateProductRequest request) {
            return new Context(request);
        }

        public long getCategoryRequestId() {
            return request.category().id().toLong();
        }

        public boolean isNewCategory() {
            return Objects.isNull(request.category().id());
        }
    }
}
