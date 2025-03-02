package br.com.fiap.soat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "processamento")
public class ProcessamentoJpa implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_video", nullable = false)
  private Long numeroVideo;

  @Column(name = "nome_video", nullable = false)
  private String nomeVideo;

  @Column(name = "usuario", nullable = false)
  private UsuarioJpa usuario;

  @Column(name = "status_processamento", nullable = false)
  private StatusProcessamento statusProcessamento;

  @Column(name = "mensagem_erro")
  private String mensagemErro;

  @Column(name = "link_download")
  private String linkDownload;

  @Column(name = "timestamp_inicio", nullable = false)
  private LocalDateTime timestampInicio;

  @Column(name = "timestamp_conclusao")
  private LocalDateTime timestampConclusao;
}
