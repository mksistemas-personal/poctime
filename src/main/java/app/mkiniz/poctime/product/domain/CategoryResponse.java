package app.mkiniz.poctime.product.domain;

import app.mkiniz.poctime.product.domain.category.Category;
import com.github.f4b6a3.tsid.Tsid;

public record CategoryResponse(String id, String name) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(Tsid.from(category.getId()).toLowerCase(), category.getName());
    }
}
