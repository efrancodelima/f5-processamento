package br.com.fiap.soat.service.provider;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.dto.SucessoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import br.com.fiap.soat.service.other.ProcessamentoService;
import br.com.fiap.soat.util.LoggerAplicacao;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class FinalizarComSucessoService {

  // Atributos
  private static final int DURACAO_LINK_MINUTOS = 24 * 60; // 24 horas

  private final AwsConfig awsConfig;
  private final ProcessamentoService procService;

  // Construtor
  @Autowired
  public FinalizarComSucessoService(AwsConfig awsConfig, ProcessamentoService procService) {
    this.awsConfig = awsConfig;
    this.procService = procService;
  }

  // Método público
  @Async
  public CompletableFuture<Boolean> finalizar(SucessoDto requisicao) throws BadGatewayException {

    Optional<ProcessamentoJpa> processamentoOpt = 
          procService.getProcessamento(requisicao.getJobId());
    
    if (!processamentoOpt.isPresent()) {
      LoggerAplicacao.error("Job ID não encontrado: " + requisicao.getJobId());
      return CompletableFuture.completedFuture(false);
    }

    ProcessamentoJpa processamento = processamentoOpt.get();
    String linkDownload;

    try {
      linkDownload = gerarLinkParaDownload(requisicao.getFilePath());
    } catch (Exception e) {
      procService.registrarErro(processamento, e.getMessage());
      return CompletableFuture.completedFuture(false);
    }

    procService.registrarConclusao(processamento, linkDownload);
    return CompletableFuture.completedFuture(true);
  }

  // Método privado
  private String gerarLinkParaDownload(String objectKey)
      throws ApplicationException {
    S3Presigner s3Presigner = awsConfig.buildS3Presigner();

    try {
      // Cria a requisição
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .getObjectRequest(req -> req.bucket(awsConfig.getBucketName()).key(objectKey))
          .signatureDuration(Duration.ofMinutes(DURACAO_LINK_MINUTOS))
          .build();

      // Executa a requisição e retorna
      PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    
    } catch (RuntimeException e) {
      throw new ApplicationException(ApplicationMessage.GERAR_LINK);
    
    } finally {
      s3Presigner.close();  
    }
  }
}
