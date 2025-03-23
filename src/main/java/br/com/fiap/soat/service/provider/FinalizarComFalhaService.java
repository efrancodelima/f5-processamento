package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.dto.FalhaDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.service.util.ProcessamentoService;
import br.com.fiap.soat.util.LoggerAplicacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FinalizarComFalhaService {

  // Atributos
  private final ProcessamentoService procService;

  // Construtor
  @Autowired
  public FinalizarComFalhaService(ProcessamentoService procService) {
    this.procService = procService;
  }
  
  // Método público
  @Async
  public void processarRequisicao(FalhaDto requisicao) {
    ProcessamentoJpa processamento;
    try {
      processamento = procService.getProcessamento(requisicao.getJobId()).get();
    } catch (Exception e) {
      LoggerAplicacao.error("Job ID não encontrado!");
      return;
    }

    procService.registrarErro(processamento, getMensagemErro(requisicao));
  }

  // Método privado
  private String getMensagemErro(FalhaDto requisicao) {

    String mensagemErro = requisicao.getErrorMessage();

    if (requisicao.getErrorCode() == 1010) {
      mensagemErro = "Não foi possível processar a requisição: arquivo inválido.";
    }

    return mensagemErro;
  }
}
