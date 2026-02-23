package app.mkiniz.poctime.shared.specification;

import jakarta.persistence.criteria.*;
import net.kaczmarzyk.spring.data.jpa.domain.PathSpecification;
import net.kaczmarzyk.spring.data.jpa.utils.QueryContext;

import java.util.Objects;

public class JsonPathLike<T> extends PathSpecification<T> {

    private final String expectedValue;
    private final String jsonField;
    private final String jsonPath;

    public JsonPathLike(QueryContext queryContext, String path, String[] args) {
        super(queryContext, path);
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("Expected value is required");
        }
        this.expectedValue = args[0];

        // O path aqui geralmente será algo como "document.identifier"
        // Vamos separar o campo da entidade e o path interno do JSON
        int firstDot = path.indexOf('.');
        if (firstDot == -1) {
            this.jsonField = path;
            this.jsonPath = "identifier"; // default se não especificado conforme seu caso de uso
        } else {
            this.jsonField = path.substring(0, firstDot);
            this.jsonPath = path.substring(firstDot + 1);
        }
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Expression<String> jsonFunction = cb.function(
                "jsonb_extract_path_text",
                String.class,
                root.get(jsonField),
                cb.literal(jsonPath)
        );
        return cb.like(jsonFunction, "%" + expectedValue + "%");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JsonPathLike<?> that = (JsonPathLike<?>) o;
        return Objects.equals(expectedValue, that.expectedValue) &&
                Objects.equals(jsonField, that.jsonField) &&
                Objects.equals(jsonPath, that.jsonPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expectedValue, jsonField, jsonPath);
    }
}
