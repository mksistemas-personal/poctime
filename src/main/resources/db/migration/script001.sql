-- Função imutável para converter o JSONB de IDs em texto para o tsvector
CREATE OR REPLACE FUNCTION immutable_jsonb_text(j jsonb) RETURNS text AS
$$
SELECT string_agg(v, ' ')
FROM jsonb_array_elements_text(j) as v;
$$ LANGUAGE sql IMMUTABLE;

-- Primeiro, removemos a coluna se ela já existir para recriá-la como gerada
ALTER TABLE economicgroup
    DROP COLUMN IF EXISTS search_vector;

-- Adicionamos a coluna como GENERATED ALWAYS
-- Ela combina o nome, descrição e os valores dentro do array JSONB de organization-ids
ALTER TABLE economicgroup
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('portuguese', coalesce(name, '')), 'A') ||
        setweight(to_tsvector('portuguese', coalesce(description, '')), 'B') ||
        setweight(to_tsvector('portuguese', coalesce(immutable_jsonb_text("organization-ids"), '')), 'C')
        ) STORED;

-- Criamos um índice GIN para performance na busca
CREATE INDEX IF NOT EXISTS idx_economicgroup_search_vector ON economicgroup USING GIN (search_vector);