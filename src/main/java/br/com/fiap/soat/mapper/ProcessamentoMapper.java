package br.com.fiap.soat.mapper;

import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import java.util.ArrayList;
import java.util.List;

public class ProcessamentoMapper {

  private ProcessamentoMapper() {}

  public static ProcessamentoDto toDto(ProcessamentoJpa proc) {

    String timestampConclusao;
    
    if (proc.getTimestampConclusao() != null) {
      timestampConclusao = proc.getTimestampConclusao().toString();
    } else {
      timestampConclusao = "";
    }
    
    var resposta = new ProcessamentoDto();
    resposta.setNomeArquivo(proc.getNomeVideo());
    resposta.setStatusProcessamento(proc.getStatus().getMessage());
    resposta.setMensagemErro(proc.getMensagemErro());
    resposta.setLinkDownload(proc.getLinkDownload());
    resposta.setTimestampInicio(proc.getTimestampInicio().toString());
    resposta.setTimestampConclusao(timestampConclusao);

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
