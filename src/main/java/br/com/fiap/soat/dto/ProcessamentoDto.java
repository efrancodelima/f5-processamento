package br.com.fiap.soat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessamentoDto {

  private String nomeArquivo;

  private String statusProcessamento;

  private String timestampStatus;

  private String mensagemErro;

  private String linkDownload;
}
