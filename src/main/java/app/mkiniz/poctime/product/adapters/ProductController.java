package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.CreateProductRequest;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.product.domain.UpdateProductRequest;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addProductService.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Tsid id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(updateProductService.execute(id, request));
    }

}
