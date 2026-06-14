package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.CreateProductRequest;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.product.domain.ProductSearchRequest;
import app.mkiniz.poctime.product.domain.UpdateProductRequest;
import app.mkiniz.poctime.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/product")
@AllArgsConstructor
@Validated
public class ProductController {

    private final AddBusinessUseCase<CreateProductRequest, ProductResponse> addProductService;
    private final UpdateBusinessUseCase<Tsid, UpdateProductRequest, ProductResponse> updateProductService;
    private final DeleteBusinessUseCase<Tsid, ProductResponse> deleteProductService;
    private final GetByIdBusinessUseCase<Tsid, ProductResponse> getProductByIdService;
    private final GetAllBusinessUseCase<ProductSearchRequest, Maybe<Slice<ProductResponse>>> getAllProductService;

    @GetMapping
    public ResponseEntity<Slice<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String categoryName,
            Pageable pageable) {
        ProductSearchRequest request = ProductSearchRequest.builder()
                .name(name)
                .sku(sku)
                .category(categoryName)
                .build();
        return getAllProductService.execute(pageable, request)
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Tsid id) {
        return ResponseEntity.ok(getProductByIdService.execute(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addProductService.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Tsid id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(updateProductService.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Tsid id) {
        return ResponseEntity.ok(deleteProductService.execute(id));
    }

}
