INSERT INTO usuario (nome, email)
VALUES ('User Test', 'email@email.com');

INSERT INTO
  processamento (nome_video, usuario_id, job_id, status, mensagem_erro, link_download, timestamp_inicio, timestamp_conclusao)
VALUES 
  ('video_01.mp4', 1, 'job_001', 'CONCLUIDO', NULL, 'https://example.com/download/video_01', '2025-03-30 12:00:00', '2025-03-30 12:30:00'),
  ('video_02.mp4', 1, 'job_002', 'ERRO', 'Falha na conversão do formato', NULL, '2025-03-30 13:00:00', NULL),
  ('video_03.mp4', 1, 'job_003', 'PROCESSANDO', NULL, NULL, '2025-03-30 14:00:00', NULL);
