CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS processamento (
    numero_video BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_video VARCHAR(255) NOT NULL,
    usuario_id BIGINT NOT NULL,
    job_id VARCHAR(255),
    status_processamento VARCHAR(255) NOT NULL,
    mensagem_erro TEXT,
    link_download VARCHAR(500),
    timestamp_inicio DATETIME NOT NULL,
    timestamp_conclusao DATETIME,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
