package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.ProductConstants;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        Tsid id,
        @NotNull(message = ProductConstants.CATEGORY_NAME_NOT_NULL)
        String name) {
}
