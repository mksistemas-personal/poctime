package app.mkiniz.poctime.product.domain.validation;

import app.mkiniz.poctime.product.domain.CategoryRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class CategoryValidator implements ConstraintValidator<ValidCategory, CategoryRequest> {

    @Override
    public boolean isValid(CategoryRequest request, ConstraintValidatorContext context) {
        if (Objects.isNull(request)) {
            return false;
        }
        boolean idIsNull = Objects.isNull(request.id());
        boolean nameIsBlank = !StringUtils.hasText(request.name());

        if (idIsNull && nameIsBlank) {
            return false;
        }
        return idIsNull || nameIsBlank;
    }
}
