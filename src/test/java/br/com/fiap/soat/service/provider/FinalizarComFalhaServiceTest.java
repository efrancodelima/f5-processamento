package br.com.fiap.soat.service.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.dto.FalhaDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.service.other.ProcessamentoService;

class FinalizarComFalhaServiceTest {

  AutoCloseable closeable;

  @Mock
  ProcessamentoService procService;

  @InjectMocks
  FinalizarComFalhaService service;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveRegistrarFalhaProcessamento() throws BadGatewayException {

    // Arrange
    String jobId = "job-id";
    String errorMessage = "";

    FalhaDto requisicao = new FalhaDto();
    requisicao.setJobId(jobId);
    requisicao.setErrorMessage(errorMessage);
    
    ProcessamentoJpa processamento = new ProcessamentoJpa();
    doReturn(Optional.of(processamento)).when(procService).getProcessamento(jobId);

    // Act
    CompletableFuture<Boolean> resultFuture = service.finalizar(requisicao);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(true, result);
    verify(procService).registrarErro(processamento, requisicao.getErrorMessage());
  }

  @Test
  void deveRetornarFalseSeNaoEncontrarJobId() throws BadGatewayException {

    // Arrange
    String jobId = "job-id";
    
    FalhaDto requisicao = new FalhaDto();
    requisicao.setJobId(jobId);
    
    doReturn(Optional.empty()).when(procService).getProcessamento(jobId);

    // Act
    CompletableFuture<Boolean> resultFuture = service.finalizar(requisicao);
    Boolean result = resultFuture.join();

    // Assert
    assertNotNull(result);
    assertEquals(false, result);
  }
}
