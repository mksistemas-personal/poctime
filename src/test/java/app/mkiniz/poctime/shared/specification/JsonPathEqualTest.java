package app.mkiniz.poctime.shared.specification;

import app.mkiniz.poctime.person.domain.Person;
import jakarta.persistence.criteria.*;
import net.kaczmarzyk.spring.data.jpa.web.DefaultQueryContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JsonPathEqualTest {

    @Test
    void toPredicateTest() {
        // Arrange
        String path = "document.identifier";
        String expectedValue = "12345678901";
        JsonPathLike<Person> spec = new JsonPathLike<>(new DefaultQueryContext(), path, new String[]{expectedValue});

        Root<Person> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> documentPath = mock(Path.class);
        Expression<String> jsonFunction = mock(Expression.class);
        Predicate equalPredicate = mock(Predicate.class);

        when(root.get("document")).thenReturn(documentPath);
        when(cb.literal("identifier")).thenReturn(mock(Expression.class));
        when(cb.function(eq("jsonb_extract_path_text"), eq(String.class), any(), any())).thenReturn(jsonFunction);
        when(cb.equal(eq(jsonFunction), eq(expectedValue))).thenReturn(equalPredicate);

        // Act
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        assertEquals(equalPredicate, result);
        verify(root).get("document");
        verify(cb).function(eq("jsonb_extract_path_text"), eq(String.class), eq(documentPath), any());
        verify(cb).equal(eq(jsonFunction), eq(expectedValue));
    }
}
