package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.domain.validation.ValidCategory;
import com.github.f4b6a3.tsid.Tsid;

@ValidCategory
public record CategoryRequest(
        Tsid id,
        String name) {
}
