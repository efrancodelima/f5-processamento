package br.com.fiap.soat.service.other;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.service.consumer.NotificacaoService;
import br.com.fiap.soat.util.LoggerAplicacao;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessamentoService {

  private final NotificacaoService notificacaoService;
  private final ProcessamentoRepository repository;

  // Construtor
  @Autowired
  public ProcessamentoService(ProcessamentoRepository repository,
      NotificacaoService notificacaoService) {
    this.repository = repository;
    this.notificacaoService = notificacaoService;
  }

  // Métodos públicos
  public Optional<ProcessamentoJpa> getProcessamento(String jobId) {
    return repository.findByJobId(jobId);
  }

  public ProcessamentoJpa registrarRecebimento(FileWrapper video, UsuarioJpa usuario) {
    var processamento = ProcessamentoJpa.builder()
        .nomeVideo(video.getName())
        .usuario(usuario)
        .statusProcessamento(StatusProcessamento.RECEBIDO)
        .timestampInicio(LocalDateTime.now())
        .build();

    return repository.save(processamento);
  }

  public void registrarProcessamento(ProcessamentoJpa processamento, String jobId) {
    processamento.setStatusProcessamento(StatusProcessamento.PROCESSANDO);
    processamento.setJobId(jobId);
    repository.save(processamento);
  }

  public void registrarErro(ProcessamentoJpa processamento, String msgErro) {
    processamento.setStatusProcessamento(StatusProcessamento.ERRO);
    processamento.setMensagemErro(msgErro);
    processamento.setTimestampConclusao(LocalDateTime.now());
    repository.save(processamento);

    EmailDto dadosEmail = EmailDto.getEmailFalha(processamento.getNomeVideo(),
        processamento.getUsuario().getEmail(), msgErro);
    
    notificarUsuario(processamento, dadosEmail);
  }

  public void registrarConclusao(ProcessamentoJpa processamento, String linkArquivo) {
    processamento.setStatusProcessamento(StatusProcessamento.SUCESSO);
    processamento.setLinkDownload(linkArquivo);
    processamento.setTimestampConclusao(LocalDateTime.now());
    repository.save(processamento);

    EmailDto dadosEmail = EmailDto.getEmailSucesso(processamento.getNomeVideo(),
          processamento.getUsuario().getEmail(), linkArquivo);

    notificarUsuario(processamento, dadosEmail);
  }

  // Método privado
  private void notificarUsuario(ProcessamentoJpa processamento, EmailDto dadosEmail) {
    try {
      notificacaoService.enviarEmail(dadosEmail);
    } catch (Exception e) {
      LoggerAplicacao.error("Erro ao notificar o usuário: " + e.getMessage());
      LoggerAplicacao.error(processamento.toString());
    }
  }
}
