package app.mkiniz.poctime.shared.repository;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;

public class PostgreSQLDialectCustom extends PostgreSQLDialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        functionContributions.getFunctionRegistry().registerPattern(
                "ts_match",
                "(?1 @@ ?2)"
        );

        functionContributions.getFunctionRegistry().registerPattern(
                "jsonb_extract_path_text",
                "jsonb_extract_path_text(?1, ?2)"
        );
    }
}
