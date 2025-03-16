package br.com.fiap.soat.service.util;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.service.consumer.NotificacaoService;
import br.com.fiap.soat.util.LoggerAplicacao;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

  private final NotificacaoService notificacaoService;
  private final ProcessamentoRepository repository;

  // Construtor
  @Autowired
  public RegistroService(ProcessamentoRepository repository,
      NotificacaoService notificacaoService) {
    this.repository = repository;
    this.notificacaoService = notificacaoService;
  }

  // Métodos públicos
  public ProcessamentoJpa registrarInicio(FileWrapper video, UsuarioJpa usuario) {
    var processamento = ProcessamentoJpa.builder()
        .nomeVideo(video.getName())
        .usuario(usuario)
        .statusProcessamento(StatusProcessamento.RECEBIDO)
        .timestampInicio(LocalDateTime.now())
        .build();

    return repository.save(processamento);
  }

  public void registrarErro(ProcessamentoJpa processamento, String msgErro) {
    processamento.setStatusProcessamento(StatusProcessamento.ERRO);
    processamento.setMensagemErro(msgErro);
    processamento.setTimestampConclusao(LocalDateTime.now());
    repository.save(processamento);

    notificarFalhaAoUsuario(processamento.getNomeVideo(), msgErro);
  }
  
  public void registrarConclusao(ProcessamentoJpa processamento, String linkArquivo) {
    processamento.setStatusProcessamento(StatusProcessamento.SUCESSO);
    processamento.setLinkDownload(linkArquivo);
    processamento.setTimestampConclusao(LocalDateTime.now());
    repository.save(processamento);

    notificarSucessoAoUsuario(processamento.getNomeVideo(), linkArquivo);
  }

  public void registrarJob(ProcessamentoJpa processamento, String jobId) {
    processamento.setStatusProcessamento(StatusProcessamento.PROCESSANDO);
    processamento.setJobId(jobId);
    repository.save(processamento);
  }

  // Métodos privados
  private void notificarFalhaAoUsuario(String nomeArquivo, String msgErro) {
    try {
      var dadosEmail = EmailDto.getEmailFalha(nomeArquivo, "email@email.com.br", msgErro);
      notificacaoService.enviarEmail(dadosEmail);
    
    } catch (Exception e) {
      LoggerAplicacao.error(e.getMessage());
      LoggerAplicacao.error(e.getStackTrace().toString());
    }
  }

  private void notificarSucessoAoUsuario(String nomeArquivo, String linkArquivo) {
    try {
      var dadosEmail = EmailDto.getEmailSucesso(nomeArquivo, "email@email.com.br", linkArquivo);
      notificacaoService.enviarEmail(dadosEmail);
    
    } catch (Exception e) {
      LoggerAplicacao.error(e.getMessage());
      LoggerAplicacao.error(e.getStackTrace().toString());
    }
  }
}
