package app.mkiniz.poctime.product.domain.validation;

import app.mkiniz.poctime.product.domain.CategoryRequest;
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

class CategoryValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValidWhenIdIsNullAndNameIsProvided() {
        CategoryRequest request = new CategoryRequest(null, "Category Name");
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Deveria ser válido quando id é nulo e nome é informado");
    }

    @Test
    void shouldBeInvalidWhenIdIsNullAndNameIsNull() {
        CategoryRequest request = new CategoryRequest(null, null);
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deveria ser inválido quando id é nulo e nome é nulo");
    }

    @Test
    void shouldBeInvalidWhenIdIsNullAndNameIsBlank() {
        CategoryRequest request = new CategoryRequest(null, "   ");
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deveria ser inválido quando id é nulo e nome é em branco");
    }

    @Test
    void shouldBeValidWhenIdIsProvidedAndNameIsNull() {
        CategoryRequest request = new CategoryRequest(Tsid.fast(), null);
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Deveria ser válido quando id é informado e nome é nulo");
    }

    @Test
    void shouldBeValidWhenIdIsProvidedAndNameIsBlank() {
        CategoryRequest request = new CategoryRequest(Tsid.fast(), "");
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Deveria ser válido quando id é informado e nome é vazio");
    }

    @Test
    void shouldBeInvalidWhenIdIsProvidedAndNameIsProvided() {
        CategoryRequest request = new CategoryRequest(Tsid.fast(), "Category Name");
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deveria ser inválido quando id é informado e nome também é informado");
    }
}
