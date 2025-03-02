CREATE TABLE IF NOT EXISTS registro_producao (
    numero_pedido BIGINT NOT NULL,
    status_pedido VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    PRIMARY KEY (numero_pedido, status_pedido)
);
