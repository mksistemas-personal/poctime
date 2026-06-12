package app.mkiniz.poctime.product.domain.category;

import com.github.f4b6a3.tsid.Tsid;
import lombok.Builder;

@Builder
public record CategoryEvent(Tsid id, String name) {
    public static CategoryEvent from(Category category) {
        return new CategoryEvent(Tsid.from(category.getId()), category.getName());
    }
}
