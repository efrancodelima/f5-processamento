CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS processamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_video VARCHAR(255) NOT NULL,
    usuario_id BIGINT NOT NULL,
    job_id VARCHAR(255),
    status ENUM('RECEBIDO', 'PROCESSANDO', 'CONCLUIDO', 'ERRO') NOT NULL,
    mensagem_erro VARCHAR(400),
    link_download VARCHAR(400),
    timestamp_inicio DATETIME NOT NULL,
    timestamp_conclusao DATETIME,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
