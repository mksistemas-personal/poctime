package app.mkiniz.poctime.product.adapters;

import app.mkiniz.poctime.product.domain.tax.CreateProductTaxRequest;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.product.domain.tax.UpdateProductTaxRequest;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/product/tax")
@AllArgsConstructor
@Validated
public class ProductTaxController {

    private final AddBusinessUseCase<CreateProductTaxRequest, ProductTaxResponse> addProductTaxService;
    private final UpdateBusinessUseCase<Tsid, UpdateProductTaxRequest, ProductTaxResponse> updateProductTaxService;
    private final DeleteBusinessUseCase<Tsid, ProductTaxResponse> deleteProductTaxService;

    @PostMapping
    public ResponseEntity<ProductTaxResponse> createProductTaxData(@Valid @RequestBody CreateProductTaxRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addProductTaxService.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductTaxResponse> updateProductTaxData(@PathVariable Tsid id, @Valid @RequestBody UpdateProductTaxRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(updateProductTaxService.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductTaxResponse> deleteProductTaxData(@PathVariable Tsid id, @Valid @RequestBody UpdateProductTaxRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(deleteProductTaxService.execute(id));
    }
}
