package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.tax.CreateProductTaxRequest;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
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
@RequestMapping(path = "/api/product/tax")
@AllArgsConstructor
@Validated
public class ProductTaxController {

    private final AddBusinessUseCase<CreateProductTaxRequest, ProductTaxResponse> addProductTaxService;

    @PostMapping
    public ResponseEntity<ProductTaxResponse> createProduct(@Valid @RequestBody CreateProductTaxRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addProductTaxService.execute(request));
    }
}
