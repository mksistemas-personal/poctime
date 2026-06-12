package app.mkiniz.poctime.product.domain.validation;

import app.mkiniz.poctime.product.ProductConstants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CategoryValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {
    String message() default ProductConstants.CATEGORY_NAME_NOT_NULL;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
