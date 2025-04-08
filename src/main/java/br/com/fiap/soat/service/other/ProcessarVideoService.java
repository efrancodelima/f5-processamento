package br.com.fiap.soat.service.other;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import br.com.fiap.soat.service.consumer.CriarJobService;
import br.com.fiap.soat.util.LoggerAplicacao;
import br.com.fiap.soat.util.SalvarArquivo;
import br.com.fiap.soat.wrapper.FileWrapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ProcessarVideoService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  
  private final ProcessamentoService processamentoService;
  private final CriarJobService criarJobService;
  private final AwsConfig awsConfig;
  private final S3Client s3Client;
  
  // Construtor
  @Autowired
  public ProcessarVideoService(ProcessamentoService processamentoService,
      CriarJobService criarJobService, AwsConfig awsConfig, S3Client s3Client) {

    this.processamentoService = processamentoService;
    this.criarJobService = criarJobService;
    this.awsConfig = awsConfig;
    this.s3Client = s3Client;
  }

  // Método público
  @Async
  public CompletableFuture<Boolean> processar(FileWrapper video, UsuarioJpa usuario) {

    ProcessamentoJpa processamento = processamentoService.registrarRecebimento(video, usuario);
    
    String diretorioLocal = TEMP_DIR + UUID.randomUUID().toString() + "/";

    String diretorioS3 = usuario.getId().toString() + "/" + processamento.getNumeroVideo() + "/";
    String caminhoVideoS3 = diretorioS3 + "input/" + video.getName();
    String diretorioOutputS3 = diretorioS3 + "output/";

    try {

      verificarConteudoVideo(video);

      File videoFile = salvarVideo(video, diretorioLocal);

      enviarVideoParaS3(caminhoVideoS3, videoFile.toPath());

      apagarArquivo(videoFile);
      
      String jobId = criarJob(caminhoVideoS3, diretorioOutputS3);

      processamentoService.registrarProcessamento(processamento, jobId);

      return CompletableFuture.completedFuture(true);

    } catch (Exception e) {
      processamentoService.registrarErro(processamento, e.getMessage());
      return CompletableFuture.completedFuture(false);
    }
  }

  // Métodos privados
  private void verificarConteudoVideo(FileWrapper video) throws ApplicationException {
    if (video.getContent() == null || video.getContent().length == 0) {
      throw new ApplicationException(ApplicationMessage.LER_ARQUIVO.getMessage()
          + video.getName() + ".");
    }
  }

  private File salvarVideo(FileWrapper video, String diretorio) throws ApplicationException {
    try {
      return SalvarArquivo.salvar(video, diretorio);
    } catch (Exception e) {
      throw new ApplicationException(ApplicationMessage.SALVAR_VIDEO);
    }
  }

  private void enviarVideoParaS3(String caminhoVideoS3, Path localPath)
      throws ApplicationException {
    try {
      PutObjectRequest putRequest = PutObjectRequest.builder()
          .bucket(awsConfig.getBucketName())
          .key(caminhoVideoS3)
          .build();

      s3Client.putObject(putRequest, RequestBody.fromFile(localPath));
    
    } catch (RuntimeException e) {
      throw new ApplicationException(ApplicationMessage.ENVIAR_S3);
    }
  }

  private void apagarArquivo(File arquivo) {
    Path path = arquivo.toPath();
    try {
      Files.delete(path);
    } catch (IOException e) {
      LoggerAplicacao.info("Não foi possível remover o arquivo: " + arquivo.getAbsolutePath());
    }
  }

  private String criarJob(String caminhoVideoS3, String diretorioImagensS3)
      throws ApplicationException {
    try {
      CreateJobResponse response = criarJobService
          .criarJob(caminhoVideoS3, diretorioImagensS3);
      return response.job().id();

    } catch (Exception e) {
      throw new ApplicationException(ApplicationMessage.CRIAR_JOB);
    }
  }
}
