package br.com.fiap.soat.service.other;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import br.com.fiap.soat.service.consumer.CriarJobService;
import br.com.fiap.soat.service.other.ProcessamentoService;
import br.com.fiap.soat.service.other.ProcessarVideoService;
import br.com.fiap.soat.util.SalvarArquivo;
import br.com.fiap.soat.wrapper.FileWrapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.Job;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class ProcessarVideoServiceTest {
  
  AutoCloseable closeable;

  @Mock
  ProcessamentoService procService;

  @Mock
  AwsConfig awsConfig;

  @Mock
  S3Client s3Client;

  @Mock
  CriarJobService criarJobService;

  @Mock
  ProcessamentoJpa processamento;
  
  @Mock
  FileWrapper video;
  
  @Mock
  UsuarioJpa usuario;

  @Mock
  PutObjectResponse putObjectResponse;

  @Mock
  CreateJobResponse createJobResponse;

  @Mock
  Job job;
  
  @InjectMocks
  ProcessarVideoService processarVideoService;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveRegistrarProcessamentoVideo() throws Exception {

    // Arrange
    doReturn(1L).when(processamento).getNumeroVideo();

    doReturn(processamento).when(procService).registrarRecebimento(video, usuario);

    doReturn("video-content".getBytes()).when(video).getContent();

    doReturn(putObjectResponse).when(s3Client)
        .putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));
    
    doReturn(createJobResponse).when(criarJobService)
        .criarJob(Mockito.anyString(), Mockito.anyString());

    String jobId = "job-id";
    doReturn(job).when(createJobResponse).job();
    doReturn(jobId).when(job).id();
    
    // Act
    CompletableFuture<Boolean> resultFuture = processarVideoService.processar(video, usuario);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(true, result);
    verify(procService).registrarProcessamento(processamento, jobId);
  }

  @Test
  void deveRegistrarErroQuandoContentVideoForNulo() throws Exception {

    // Arrange
    doReturn(1L).when(processamento).getNumeroVideo();

    doReturn(processamento).when(procService).registrarRecebimento(video, usuario);

    String nomeVideo = "video-01.mp4";
    doReturn(nomeVideo).when(video).getName();
    doReturn(null).when(video).getContent();
    
    doReturn(putObjectResponse).when(s3Client)
        .putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));
    
    doReturn(createJobResponse).when(criarJobService)
        .criarJob(Mockito.anyString(), Mockito.anyString());

    doReturn(job).when(createJobResponse).job();
    doReturn("job-id").when(job).id();
    
    // Act
    CompletableFuture<Boolean> resultFuture = processarVideoService.processar(video, usuario);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(false, result);
    verify(procService)
        .registrarErro(processamento, ApplicationMessage.lerArquivo.getMessage()
          + video.getName() + ".");
  }

  @Test
  void deveRegistrarErroQuandoSalvarVideoFalhar() throws Exception {

    try (MockedStatic<SalvarArquivo> salvarArquivoMock = Mockito.mockStatic(SalvarArquivo.class)) {
      
      // Arrange
      salvarArquivoMock.when(
        () -> SalvarArquivo.salvar(Mockito.any(), Mockito.anyString())
      ).thenThrow(new IOException());

      doReturn(1L).when(processamento).getNumeroVideo();

      doReturn(processamento).when(procService).registrarRecebimento(video, usuario);

      doReturn("video-content".getBytes()).when(video).getContent();

      // Act
      doReturn(job).when(createJobResponse).job();
      doReturn("job-id").when(job).id();
      
      CompletableFuture<Boolean> resultFuture = processarVideoService.processar(video, usuario);
      Boolean result = resultFuture.join();

      // Assert
      assertNotNull(result);
      assertEquals(false, result);     
      verify(procService)
          .registrarErro(processamento, ApplicationMessage.salvarVideo.getMessage());
    }
  }

  @Test
  void deveRegistrarErroQuandoEnviarVideoParaS3Falhar() throws Exception {

    // Arrange
    doReturn(1L).when(processamento).getNumeroVideo();

    doReturn(processamento).when(procService).registrarRecebimento(video, usuario);

    doReturn("video-content".getBytes()).when(video).getContent();

    doThrow(new RuntimeException()).when(s3Client)
        .putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));
    
    // Act
    doReturn(job).when(createJobResponse).job();
    doReturn("job-id").when(job).id();
    
    CompletableFuture<Boolean> resultFuture = processarVideoService.processar(video, usuario);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(false, result);
    verify(procService)
        .registrarErro(processamento, ApplicationMessage.enviarS3.getMessage());
  }

  @Test
  void deveRegistrarErroQuandoCriarJobFalhar() throws Exception {

    // Arrange
    doReturn(1L).when(processamento).getNumeroVideo();

    doReturn(processamento).when(procService).registrarRecebimento(video, usuario);

    doReturn("video-content".getBytes()).when(video).getContent();

    doReturn(putObjectResponse).when(s3Client)
        .putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));
    
    doThrow(new Exception()).when(criarJobService)
        .criarJob(Mockito.anyString(), Mockito.anyString());

    // Act
    doReturn(job).when(createJobResponse).job();
    doReturn("job-id").when(job).id();
    
    CompletableFuture<Boolean> resultFuture = processarVideoService.processar(video, usuario);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(false, result);
    verify(procService)
        .registrarErro(processamento, ApplicationMessage.criarJob.getMessage());
  }
}
