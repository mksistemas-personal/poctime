package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.CreateProductRequest;
import app.mkiniz.poctime.product.domain.ProductResponse;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/product")
@AllArgsConstructor
@Validated
public class ProductController {

    private final AddBusinessUseCase<CreateProductRequest, ProductResponse> addProductService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addProductService.execute(request));
    }


}
