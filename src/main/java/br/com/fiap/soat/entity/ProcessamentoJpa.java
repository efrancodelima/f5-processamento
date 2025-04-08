package br.com.fiap.soat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "nome_video", nullable = false)
  private String nomeVideo;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "usuario_id", nullable = false)
  private UsuarioJpa usuario;

  @Column(name = "job_id")
  private String jobId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private StatusProcessamento status;

  @Column(name = "mensagem_erro")
  private String mensagemErro;

  @Column(name = "link_download", length = 500)
  private String linkDownload;

  @Column(name = "timestamp_inicio", nullable = false)
  private LocalDateTime timestampInicio;

  @Column(name = "timestamp_conclusao")
  private LocalDateTime timestampConclusao;

  @Override
  public String toString() {
    return "ProcessamentoJpa { "
        + "id=" + id
        + ", nomeVideo=" + nomeVideo
        + ", usuario=" + (usuario != null ? usuario.toString() : "null")
        + ", jobId=" + jobId
        + ", status=" + status
        + ", mensagemErro=" + mensagemErro
        + ", linkDownload=" + linkDownload
        + ", timestampInicio=" + timestampInicio
        + ", timestampConclusao=" + timestampConclusao
        + " }";
  }
}
