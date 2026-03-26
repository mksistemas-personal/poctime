package app.mkiniz.poctime.product.services;

import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllProductService implements GetAllBusinessUseCase<Specification<Product>, Maybe<Slice<ProductResponse>>> {
    private final ProductRepository productRepository;

    @Override
    public Maybe<Slice<ProductResponse>> execute(Pageable pageable, @Nullable Specification<Product> productSpecification) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by("id")));

        return Maybe.fromEval(later(() -> Objects.nonNull(productSpecification) ?
                        productRepository.findAll(productSpecification, pageableWithSort) :
                        productRepository.findAll(pageableWithSort)))
                .filter(Slice::hasContent)
                .map(products ->
                        new SliceImpl<>(products.map(ProductResponse::from).toList(),
                                pageableWithSort,
                                products.hasNext()));
    }
}
