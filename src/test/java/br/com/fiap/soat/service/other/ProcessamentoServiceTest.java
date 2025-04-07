package br.com.fiap.soat.service.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.service.consumer.NotificacaoService;
import br.com.fiap.soat.wrapper.FileWrapper;

class ProcessamentoServiceTest {

  AutoCloseable closeable;

  @Captor
  ArgumentCaptor<ProcessamentoJpa> repositoryArgCaptor;

  @Captor
  ArgumentCaptor<EmailDto> notificacaoArgCaptor;

  @Mock
  NotificacaoService notificacaoService;

  @Mock
  ProcessamentoRepository repository;

  @InjectMocks
  ProcessamentoService service;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveRetornarProcessamentoPeloJobId() {
    // Arrange
    ProcessamentoJpa proc = new ProcessamentoJpa();
    doReturn(Optional.of(proc)).when(repository).findByJobId(Mockito.anyString());

    // Act
    Optional<ProcessamentoJpa> resposta = service.getProcessamento("job-id");

    // Assert
    assertEquals(true, resposta.isPresent());
    assertEquals(proc, resposta.get());
  }

  @Test
  void deveRegistrarRecebimento() {
    // Arrange
    ProcessamentoJpa proc = new ProcessamentoJpa();
    doReturn(proc).when(repository).save(Mockito.any());

    FileWrapper video = Mockito.mock(FileWrapper.class);
    String nomeVideo = "nome-video";
    doReturn(nomeVideo).when(video).getName();

    UsuarioJpa usuario = new UsuarioJpa();
    
    // Act
    ProcessamentoJpa resposta = service.registrarRecebimento(video, usuario);

    // Assert
    assertNotNull(resposta);
    assertEquals(proc, resposta);

    verify(repository).save(repositoryArgCaptor.capture());
    ProcessamentoJpa arg = repositoryArgCaptor.getValue();
    assertNotNull(arg);
    
    assertEquals(nomeVideo, arg.getNomeVideo());
    assertEquals(usuario, arg.getUsuario());
    assertEquals(StatusProcessamento.RECEBIDO, arg.getStatusProcessamento());
    assertNotNull(arg.getTimestampInicio());
  }

  @Test
  void deveRegistrarProcessamento() {
    // Arrange
    ProcessamentoJpa proc = new ProcessamentoJpa();
    String jobId = "job-id";
    doReturn(proc).when(repository).save(Mockito.any());

    // Act
    service.registrarProcessamento(proc, jobId);

    // Assert
    verify(repository).save(repositoryArgCaptor.capture());
    ProcessamentoJpa arg = repositoryArgCaptor.getValue();
    assertNotNull(arg);

    assertEquals(jobId, arg.getJobId());
    assertEquals(StatusProcessamento.PROCESSANDO, arg.getStatusProcessamento());
  }

  @Test
  void deveRegistrarErroENotificarUsuario() throws BadGatewayException {
    // Arrange
    String msgErro = "um erro qualquer";
    String email = "email@email.com";
    String nomeVideo = "video-01.mp4";
    
    UsuarioJpa usuario = new UsuarioJpa();
    usuario.setEmail(email);
    
    ProcessamentoJpa proc = new ProcessamentoJpa();
    proc.setUsuario(usuario);
    proc.setNomeVideo(nomeVideo);

    doReturn(proc).when(repository).save(Mockito.any());
    doNothing().when(notificacaoService).enviarEmail(Mockito.any());

    // Act
    service.registrarErro(proc, msgErro);

    // Assert
    verify(repository).save(repositoryArgCaptor.capture());
    ProcessamentoJpa processamento = repositoryArgCaptor.getValue();
    assertNotNull(processamento);

    assertEquals(StatusProcessamento.ERRO, processamento.getStatusProcessamento());
    assertEquals(msgErro, processamento.getMensagemErro());
    assertNotNull(processamento.getTimestampConclusao());

    verify(notificacaoService).enviarEmail(notificacaoArgCaptor.capture());
    EmailDto dadosEmail = notificacaoArgCaptor.getValue();
    assertNotNull(dadosEmail);

    assertEquals(email, dadosEmail.getEmailDestino());
    assertTrue(dadosEmail.getAssunto().contains("vídeo não pode ser processado"));
    assertTrue(dadosEmail.getTexto().contains(msgErro));
  }

  @Test
  void deveRegistrarConclusaoENotificarUsuario() throws BadGatewayException {
    // Arrange
    String link = "http://www.example.com";
    String email = "email@email.com";
    String nomeVideo = "video-01.mp4";
    
    UsuarioJpa usuario = new UsuarioJpa();
    usuario.setEmail(email);
    
    ProcessamentoJpa proc = new ProcessamentoJpa();
    proc.setUsuario(usuario);
    proc.setNomeVideo(nomeVideo);

    doReturn(proc).when(repository).save(Mockito.any());
    doNothing().when(notificacaoService).enviarEmail(Mockito.any());

    // Act
    service.registrarConclusao(proc, link);

    // Assert
    verify(repository).save(repositoryArgCaptor.capture());
    ProcessamentoJpa processamento = repositoryArgCaptor.getValue();
    assertNotNull(processamento);

    assertEquals(StatusProcessamento.SUCESSO, processamento.getStatusProcessamento());
    assertEquals(link, processamento.getLinkDownload());
    assertNotNull(processamento.getTimestampConclusao());

    verify(notificacaoService).enviarEmail(notificacaoArgCaptor.capture());
    EmailDto dadosEmail = notificacaoArgCaptor.getValue();
    assertNotNull(dadosEmail);

    assertEquals(email, dadosEmail.getEmailDestino());
    assertTrue(dadosEmail.getAssunto().contains("imagens estão prontas"));
    assertTrue(dadosEmail.getTexto().contains(link));
  }
}
