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

INSERT INTO categoria(id, descricao)
VALUES (1, 'Alimentação'),
       (2, 'Saúde'),
       (3, 'Moradia'),
       (4, 'Transporte'),
       (5, 'Lazer'),
       (6, 'Imprevistos'),
       (7, 'Outras');

CREATE TABLE role
(
    id   SERIAL PRIMARY KEY,
    role VARCHAR(256) NOT NULL
);
INSERT INTO role
VALUES (1, 'ADMINISTRATOR'),
       (2, 'MANAGER'),
       (3, 'USER');

CREATE TABLE usuario
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    created  DATE         NOT NULL,
    updated  DATE         NOT NULL,
    email    VARCHAR(256) NOT NULL,
    active   BOOLEAN,
    locked   BOOLEAN,
    expired  BOOLEAN
);

CREATE TABLE usuario_role
(
    usuario_id int REFERENCES usuario (id) ON UPDATE CASCADE ON DELETE CASCADE,
    role_id int REFERENCES role (id) ON UPDATE CASCADE,
    CONSTRAINT usuario_role_pkey PRIMARY KEY (usuario_id, role_id)
);

INSERT INTO usuario
VALUES (1,'admin','$2a$10$ArrM5Y7goMuLJOcf.jvq/.mRZuOR860S0DuIEWIQ3id3up4AmLyxK',now(), now(), 'admin@admin.com',true,false,false);

INSERT INTO usuario_role
VALUES (1,1);

alter sequence usuario_id_seq cache 2;
