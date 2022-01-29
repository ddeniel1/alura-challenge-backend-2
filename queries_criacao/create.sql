CREATE TABLE categoria
(
    id        SERIAL PRIMARY KEY,
    descricao VARCHAR(100)
);

CREATE TABLE receitas
(
    id        SERIAL PRIMARY KEY,
    descricao VARCHAR(256) NOT NULL,
    valor     FLOAT        NOT NULL,
    data      DATE         NOT NULL,
    categoria INT,
    CONSTRAINT fk_categoria FOREIGN KEY (categoria) REFERENCES categoria (id)
);

CREATE TABLE despesas
(
    id        SERIAL PRIMARY KEY,
    descricao VARCHAR(256) NOT NULL,
    valor     FLOAT        NOT NULL,
    data      DATE         NOT NULL,
    categoria INT,
    CONSTRAINT fk_categoria FOREIGN KEY (categoria) REFERENCES categoria (id)
);

INSERT INTO categoria(id,descricao)
VALUES (1,'Alimentação'),
       (2,'Saúde'),
       (3,'Moradia'),
       (4,'Transporte'),
       (5,'Lazer'),
       (6,'Imprevistos'),
       (7,'Outras');