package br.com.fiap.soat.service.util;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.util.LoggerAplicacao;
import br.com.fiap.soat.util.SalvarArquivo;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ProcessarVideoService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  
  private final RegistroService registroService;
  private final ExtrairImagensService extrairImagensService;
  private final AwsConfig awsConfig;
  
  // Construtor
  @Autowired
  public ProcessarVideoService(RegistroService registroService,
      ExtrairImagensService extrairImagensService, AwsConfig awsConfig) {

    this.registroService = registroService;
    this.extrairImagensService = extrairImagensService;
    this.awsConfig = awsConfig;
  }

  // Método público
  @Async
  public CompletableFuture<Boolean> execute(FileWrapper video, UsuarioJpa usuario) {

    String uniqueId = UUID.randomUUID().toString();

    String diretorioBase = TEMP_DIR + uniqueId + "/";

    ProcessamentoJpa processamento = registroService.registrarInicio(video, usuario);
    
    String caminhoVideoS3 = usuario.getId().toString() 
        + "/" + processamento.getNumeroVideo()
        + "_" + video.getName();

    try {

      verificarConteudoVideo(video, processamento);

      LoggerAplicacao.info("Conteúdo OK");

      File videoFile = salvarVideo(video, diretorioBase, processamento);

      LoggerAplicacao.info("Salvar vídeo OK");

      enviarVideoParaS3(caminhoVideoS3, videoFile.toPath(), processamento);

      LoggerAplicacao.info("Enviar vídeo para o S3 OK");
      
      videoFile.delete();

      LoggerAplicacao.info("Apagar vídeo OK");

      String jobId = iniciarJob(caminhoVideoS3, processamento);

      LoggerAplicacao.info("Iniciar job OK");
      
      registroService.registrarJob(processamento, jobId);

      LoggerAplicacao.info("Registrar job OK");
      
      return CompletableFuture.completedFuture(true);

    } catch (Exception e) {

      LoggerAplicacao.error(e.getMessage());

      registroService.registrarErro(processamento, e.getMessage());

      return CompletableFuture.completedFuture(false);
    }
  }

  private void verificarConteudoVideo(FileWrapper video, ProcessamentoJpa processamento)
      throws Exception {
    
    if (video.getContent() == null || video.getContent().length == 0) {
      String mensagem = "Não foi possível ler o arquivo " + video.getName();
      registroService.registrarErro(processamento, mensagem);
      throw new Exception(mensagem);
    }
  }

  private File salvarVideo(FileWrapper video, String diretorio, ProcessamentoJpa processamento)
      throws Exception {
    
    try {
      return SalvarArquivo.salvar(video, diretorio);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao salvar o arquivo.";
      registroService.registrarErro(processamento, mensagem);
      throw e;
    }
  }

  private void enviarVideoParaS3(String caminhoVideoS3, Path localPath,
      ProcessamentoJpa processamento) throws Exception {

    try {
      S3Client s3 = S3Client.builder()
          .region(awsConfig.pegarRegiao())
          .credentialsProvider(StaticCredentialsProvider.create(awsConfig.pegarCredenciais()))
          .build();
      
      PutObjectRequest putRequest = PutObjectRequest.builder()
          .bucket(awsConfig.getBucketVideos())
          .key(caminhoVideoS3)
          .build();

      s3.putObject(putRequest, RequestBody.fromFile(localPath));
    
    } catch (RuntimeException e) {
      String mensagem = "Ocorreu um erro ao salvar o vídeo.";
      
      throw new Exception(mensagem);
    }
  }

  private String iniciarJob(String caminhoVideoS3, ProcessamentoJpa processamento)
      throws Exception {
    
    try {
      CreateJobResponse response = extrairImagensService.iniciarJob(caminhoVideoS3);
      return response.job().id();

    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao iniciar a extração das imagens.";
      registroService.registrarErro(processamento, mensagem);
      throw new Exception(mensagem);
    }
  }
}
