package app.mkiniz.poctime.product.services;

import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.category.CategoryRepository;
import com.github.f4b6a3.tsid.TsidFactory;

public interface ServiceDefaults {
    default Category saveNewCategory(CategoryRepository repository, String name) {
        return repository.save(
                Category.builder()
                        .id(TsidFactory.newInstance256().create().toLong())
                        .name(name)
                        .build());
    }
}
