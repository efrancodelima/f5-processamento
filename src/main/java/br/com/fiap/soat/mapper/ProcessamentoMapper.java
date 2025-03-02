package br.com.fiap.soat.mapper;

import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessamentoMapper {

  private ProcessamentoMapper() {}

  public static ProcessamentoDto toDto(ProcessamentoJpa proc) {

    LocalDateTime timestamp = proc.getTimestampConclusao() != null 
        ? proc.getTimestampConclusao() : proc.getTimestampInicio();

    var resposta = new ProcessamentoDto();
    resposta.setNomeArquivo(proc.getNomeVideo());
    resposta.setStatusProcessamento(proc.getStatusProcessamento().getMessage());
    resposta.setTimestampStatus(timestamp.toString());
    resposta.setMensagemErro(proc.getMensagemErro());
    resposta.setLinkDownload(proc.getLinkDownload());

    return resposta;
  }

  public static List<ProcessamentoDto> toDto(List<ProcessamentoJpa> procs) {

    var resposta = new ArrayList<ProcessamentoDto>();

    for (var proc : procs) {
      resposta.add(toDto(proc));
    }
    return resposta;
  }
}
