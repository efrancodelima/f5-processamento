package br.com.fiap.soat.service.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.dto.SucessoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import br.com.fiap.soat.service.other.ProcessamentoService;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class FinalizarComSucessoServiceTest {

  AutoCloseable closeable;

  @Mock
  AwsConfig awsConfig;
  
  @Mock
  ProcessamentoService procService;

  @Mock
  S3Presigner s3presigner;

  @Mock
  PresignedGetObjectRequest presignedRequest;

  @InjectMocks
  FinalizarComSucessoService service;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveRegistrarConclusaoProcessamento() throws Exception {
    
    // Arrange
    String jobId = "job-id";    
    SucessoDto requisicao = new SucessoDto();
    requisicao.setJobId(jobId);

    ProcessamentoJpa processamento = new ProcessamentoJpa();
    doReturn(Optional.of(processamento)).when(procService).getProcessamento(jobId);

    doReturn("bucket-name").when(awsConfig).getBucketName();

    doReturn(presignedRequest).when(s3presigner)
        .presignGetObject(Mockito.any(GetObjectPresignRequest.class));

    String url = "https://example.com";
    doReturn(new URL(url)).when(presignedRequest).url();

    // Act
    CompletableFuture<Boolean> resultFuture = service.finalizar(requisicao);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(true, result);
    verify(procService).registrarConclusao(processamento, url);
  }

  @Test
  void deveRetornarFalseSeNaoEncontrarJobId() throws Exception {

    // Arrange
    String jobId = "job-id";

    SucessoDto requisicao = new SucessoDto();
    requisicao.setJobId(jobId);
    
    doReturn(Optional.empty()).when(procService).getProcessamento(jobId);

    // Act
    CompletableFuture<Boolean> resultFuture = service.finalizar(requisicao);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(false, result);
  }

  @Test
  void deveRetornarFalseQuandoS3PresignerFalhar() throws Exception {
    
    // Arrange
    String jobId = "job-id";
    SucessoDto requisicao = new SucessoDto();
    requisicao.setJobId(jobId);
    
    ProcessamentoJpa processamento = new ProcessamentoJpa();
    doReturn(Optional.of(processamento)).when(procService).getProcessamento(jobId);

    doReturn("bucket-name").when(awsConfig).getBucketName();

    doThrow(new RuntimeException()).when(s3presigner)
        .presignGetObject(Mockito.any(GetObjectPresignRequest.class));

    // Act
    CompletableFuture<Boolean> resultFuture = service.finalizar(requisicao);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(false, result);
    verify(procService).registrarErro(processamento, ApplicationMessage.gerarLink.getMessage());
  }
}
