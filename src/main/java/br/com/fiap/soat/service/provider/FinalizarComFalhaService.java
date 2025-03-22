package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.dto.FalhaDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.service.util.ProcessamentoService;
import br.com.fiap.soat.util.LoggerAplicacao;
import java.util.Optional;
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

    Optional<ProcessamentoJpa> processamentoOpt = 
          procService.getProcessamento(requisicao.getJobId());
    
    if (!processamentoOpt.isPresent()) {
      LoggerAplicacao.error("Job ID não encontrado!");
      return;
    }

    ProcessamentoJpa processamento = processamentoOpt.get();

    procService.registrarErro(processamento, requisicao.getErrorMessage());
  }
}
