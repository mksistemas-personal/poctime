-- Category table
CREATE TABLE IF NOT EXISTS category
(
    id      BIGINT  NOT NULL,
    name    VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

-- Person table
CREATE TABLE IF NOT EXISTS person
(
    id       BIGINT  NOT NULL,
    name     VARCHAR(255),
    document JSONB   NOT NULL,
    deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_person PRIMARY KEY (id)
);

-- Organization table
CREATE TABLE IF NOT EXISTS organization
(
    id                BIGINT  NOT NULL,
    person_id         BIGINT,
    responsible_id    BIGINT,
    responsible_email VARCHAR(255),
    street            VARCHAR(255),
    number            VARCHAR(255),
    complement        VARCHAR(255),
    neighborhood      VARCHAR(255),
    city              VARCHAR(255),
    state             VARCHAR(255),
    state_code        VARCHAR(255),
    zip_code          VARCHAR(255),
    country           VARCHAR(255),
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_organization PRIMARY KEY (id),
    CONSTRAINT fk_organization_person FOREIGN KEY (person_id) REFERENCES person (id),
    CONSTRAINT fk_organization_responsible FOREIGN KEY (responsible_id) REFERENCES person (id)
);

-- Client table
CREATE TABLE IF NOT EXISTS client
(
    id           BIGINT  NOT NULL,
    person_id    BIGINT,
    client_email VARCHAR(255),
    street       VARCHAR(255),
    number       VARCHAR(255),
    complement   VARCHAR(255),
    neighborhood VARCHAR(255),
    city         VARCHAR(255),
    state        VARCHAR(255),
    state_code   VARCHAR(255),
    zip_code     VARCHAR(255),
    country      VARCHAR(255),
    deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_client PRIMARY KEY (id),
    CONSTRAINT fk_client_person FOREIGN KEY (person_id) REFERENCES person (id)
);

-- EconomicGroup table
CREATE TABLE IF NOT EXISTS economicgroup
(
    id                 BIGINT  NOT NULL,
    name               VARCHAR(255),
    description        VARCHAR(255),
    "organization-ids" JSONB   NOT NULL,
    deleted            BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_economicgroup PRIMARY KEY (id)
);

-- Product table
CREATE TABLE IF NOT EXISTS product
(
    id          BIGINT  NOT NULL,
    name        VARCHAR(255),
    description TEXT,
    sku         VARCHAR(100),
    category_id BIGINT,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_product PRIMARY KEY (id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
);

-- ProductTaxData table
CREATE TABLE IF NOT EXISTS product_tax_data
(
    id           BIGINT       NOT NULL,
    product_id   BIGINT       NOT NULL,
    ncm          VARCHAR(10)  NOT NULL,
    cst_ipi      VARCHAR(2)   NOT NULL,
    cst_pis      VARCHAR(2)   NOT NULL,
    cst_cofins   VARCHAR(2)   NOT NULL,
    cfop         VARCHAR(4)   NOT NULL,
    product_type VARCHAR(255) NOT NULL,
    origin       VARCHAR(255) NOT NULL,
    valid_from   DATE         NOT NULL,
    valid_until  DATE,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_product_tax_data PRIMARY KEY (id),
    CONSTRAINT fk_product_tax_data_product FOREIGN KEY (product_id) REFERENCES product (id)
);

-- Envers Audit tables
CREATE TABLE IF NOT EXISTS revinfo
(
    id        INTEGER NOT NULL,
    timestamp BIGINT  NOT NULL,
    username  VARCHAR(255),
    CONSTRAINT pk_revinfo PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS product_tax_data_aud
(
    id           BIGINT  NOT NULL,
    rev          INTEGER NOT NULL,
    revtype      SMALLINT,
    product_id   BIGINT,
    ncm          VARCHAR(10),
    cst_ipi      VARCHAR(2),
    cst_pis      VARCHAR(2),
    cst_cofins   VARCHAR(2),
    cfop         VARCHAR(4),
    product_type VARCHAR(255),
    origin       VARCHAR(255),
    valid_from   DATE,
    valid_until  DATE,
    deleted      BOOLEAN,
    CONSTRAINT pk_product_tax_data_aud PRIMARY KEY (id, rev),
    CONSTRAINT fk_product_tax_data_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (id)
);

-- Sequences if any (Hibernate usually creates them if using AUTO/SEQUENCE)
-- Mas como estamos usando TSID, provavelmente não precisamos de sequências para os IDs.
-- revinfo geralmente usa uma sequência no Hibernate.
CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;
