package app.mkiniz.poctime.product.services.base;

import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.product.domain.UpdateProductRequest;
import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.category.CategoryRepository;
import app.mkiniz.poctime.product.services.ServiceDefaults;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class UpdateProductService implements UpdateBusinessUseCase<Tsid, UpdateProductRequest, ProductResponse>, ServiceDefaults {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TsidGenerator tsidGenerator;

    @Override
    public ProductResponse execute(Tsid id, UpdateProductRequest request) {
        return (ProductResponse) Either.<BusinessException, Context>right(new Context(id, request))
                .flatMap(this::findProduct)
                .flatMap(this::findCategory)
                .flatMap(this::updateProduct)
                .map(context -> ProductResponse.from(context.product))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> findProduct(Context context) {
        Optional<Product> product = productRepository.findById(context.id.toLong());
        product.ifPresent(value -> context.product = value);
        return product.isPresent() ?
                Either.right(context) :
                Either.left(new BusinessException(ProductConstants.PRODUCT_NOT_FOUND));
    }

    private Either<BusinessException, Context> findCategory(Context context) {
        if (context.categoryIsNull()) {
            return Either.left(new BusinessException(ProductConstants.CATEGORY_NOT_FOUND));
        }
        if (context.isNewCategory()) {
            context.category = saveNewCategory(categoryRepository, context.request.category().name());
            return Either.right(context);
        } else {
            Optional<Category> category = categoryRepository.findById(context.getCategoryRequestId());
            category.ifPresent(value -> context.category = value);
            return category.isPresent() ?
                    Either.right(context) :
                    Either.left(new BusinessException(ProductConstants.CATEGORY_NOT_FOUND));
        }
    }

    private Either<BusinessException, Context> updateProduct(Context context) {
        context.product.setName(context.request.name());
        context.product.setCategory(context.category);
        context.product.setSku(context.request.sku());
        context.product.setDescription(context.request.description());
        context.product.updated();
        context.product = productRepository.save(context.product);
        return Either.right(context);
    }

    private static class Context {
        public final Tsid id;
        public final UpdateProductRequest request;
        public Category category;
        public Product product;

        private Context(Tsid id, UpdateProductRequest request) {
            this.id = id;
            this.request = request;
        }

        public long getCategoryRequestId() {
            return request.category().id().toLong();
        }

        public boolean isNewCategory() {
            return Objects.isNull(request.category().id());
        }

        public boolean categoryIsNull() {
            return Objects.isNull(request.category());
        }
    }
}
