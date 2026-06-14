package app.mkiniz.poctime.product.services.base;

import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.product.domain.ProductSearchRequest;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllProductService implements GetAllBusinessUseCase<ProductSearchRequest, Maybe<Slice<ProductResponse>>> {
    private final ProductRepository productRepository;

    @Override
    public Maybe<Slice<ProductResponse>> execute(Pageable pageable, @Nullable ProductSearchRequest request) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by("id")));

        ProductSearchRequest searchRequest = Optional.ofNullable(request).orElse(ProductSearchRequest.builder().build());

        return Maybe.fromEval(later(() -> productRepository.findAll(searchRequest, pageableWithSort)))
                .filter(Slice::hasContent)
                .map(products ->
                        new SliceImpl<>(products.map(ProductResponse::from).toList(),
                                pageableWithSort,
                                products.hasNext()));
    }
}
