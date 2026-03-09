package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.ProductConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank(message = ProductConstants.NAME_NOT_BLANK)
        String name,
        @NotNull(message = ProductConstants.CATEGORY_NOT_NULL)
        CategoryRequest category,
        @NotBlank(message = ProductConstants.SKU_NOT_BLANK)
        String sku,
        String description) {

}
