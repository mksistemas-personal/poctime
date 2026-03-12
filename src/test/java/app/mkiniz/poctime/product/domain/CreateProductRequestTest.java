package app.mkiniz.poctime.product.domain;

import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateProductRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeInvalidWhenCategoryIsInvalid() {
        // ID informado e Nome informado -> Inválido de acordo com CategoryValidator
        CategoryRequest invalidCategory = new CategoryRequest(Tsid.fast(), "Some Name");
        CreateProductRequest request = new CreateProductRequest("Product", invalidCategory, "SKU123", "Description");

        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Deveria haver violações de validação para categoria inválida");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("category")),
                "Deveria haver erro no campo 'category'");
    }

    @Test
    void shouldBeValidWhenCategoryIsValid() {
        // ID nulo e Nome informado -> Válido
        CategoryRequest validCategory = new CategoryRequest(null, "New Category");
        CreateProductRequest request = new CreateProductRequest("Product", validCategory, "SKU123", "Description");

        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Não deveria haver violações para categoria válida");
    }
}
