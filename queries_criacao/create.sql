CREATE TABLE public.receitas
(
    id        SERIAL PRIMARY KEY,
    descricao VARCHAR(256) NOT NULL,
    valor     FLOAT        NOT NULL,
    data      DATE         NOT NULL
);

CREATE TABLE public.despesas
(
    id        SERIAL PRIMARY KEY,
    descricao VARCHAR(256) NOT NULL,
    valor     FLOAT        NOT NULL,
    data      DATE         NOT NULL
);