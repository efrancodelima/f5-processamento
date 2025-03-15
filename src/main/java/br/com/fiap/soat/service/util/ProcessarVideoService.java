package br.com.fiap.soat.service.util;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.util.ApagarDiretorio;
import br.com.fiap.soat.util.CompactarArquivos;
import br.com.fiap.soat.util.ExtrairImagens;
import br.com.fiap.soat.util.SalvarArquivo;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class ProcessarVideoService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  private static final int INTERVALO = 15;
  private final ProcessamentoService procService;
  private final AwsBasicCredentials credenciaisAws;
  
  ProcessamentoJpa processamento;

  // Construtor
  @Autowired
  public ProcessarVideoService(ProcessamentoService procService, AwsConfig awsConfig) {
    this.procService = procService;
    this.credenciaisAws = awsConfig.pegarCredenciais();
  }

  // Método público
  @Async
  public CompletableFuture<Boolean> execute(FileWrapper video, UsuarioJpa usuario) {

    String uniqueId = UUID.randomUUID().toString();
    String diretorioBaseStr = TEMP_DIR + uniqueId;
    String diretorioImagensStr = diretorioBaseStr + "/imagens";
    String caminhoArquivoZip = diretorioBaseStr + "/imagens.zip";
    processamento = procService.registrarInicio(video, usuario);
    String fileKeyS3 = usuario.getId().toString() + "/" + processamento.getNumeroVideo()
        + "_" + video.getName();
    
    String bucketName = "imagens-compactadas";
    
    try {
      verificarConteudoVideo(video);
      File videoFile = salvarVideo(video, diretorioBaseStr);
      extrairImagensVideo(videoFile, diretorioImagensStr);
      videoFile.delete();
      compactarImagens(diretorioImagensStr, caminhoArquivoZip);
      ApagarDiretorio.apagar(diretorioImagensStr);
      
      // Precisa das variáveis de ambiente AWS_ACCESS_KEY_ID e AWS_SECRET_ACCESS_KEY
      salvarNoBucketS3(bucketName, fileKeyS3, Paths.get(caminhoArquivoZip));
      
      // falta gerar a URL com o S3Presigner
      // É uma URL pré-assinado que tem data de validade

      procService.registrarConclusao(processamento, "https://example.com/");
      return CompletableFuture.completedFuture(true);

    } catch (Exception e) {
      return CompletableFuture.completedFuture(false);
    }
  }

  private void verificarConteudoVideo(FileWrapper video)
      throws Exception {
    
    if (video.getContent() == null || video.getContent().length == 0) {
      String mensagem = "Não foi possível ler o arquivo " + video.getName();
      procService.registrarErro(processamento, mensagem);
      throw new Exception(mensagem);
    }
  }

  private File salvarVideo(FileWrapper video, String diretorioBaseStr) throws Exception {
    try {
      return SalvarArquivo.salvar(video, diretorioBaseStr);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao salvar o arquivo.";
      procService.registrarErro(processamento, mensagem);
      throw e;
    }
  }

  private void extrairImagensVideo(File video, String diretorioImagens) throws Exception {
    try {
      ExtrairImagens.extrair(video, INTERVALO, diretorioImagens);

    } catch (Exception e) {
      String mensagem;

      if (e.getMessage().contains("Could not open input")) {
        mensagem = "O tipo de arquivo enviado não é compatível com este serviço.";
      } else {
        mensagem = "Ocorreu um erro ao extrair as imagens do vídeo.";
      }
      procService.registrarErro(processamento, mensagem);
      
      throw e;
    }
  }

  private void compactarImagens(String diretorioImagens, String caminhoArquivoZip)
      throws Exception {
    
    try {
      CompactarArquivos.compactar(diretorioImagens, caminhoArquivoZip);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao compactar as imagens.";
      procService.registrarErro(processamento, mensagem);
      
      throw e;
    }
  }

  private void salvarNoBucketS3(String bucketName, String fileKeyS3, Path localPath)
      throws Exception {

    try {
      S3Client s3 = S3Client.builder()
          .region(Region.US_EAST_1)
          .credentialsProvider(StaticCredentialsProvider.create(credenciaisAws))
          .build();
      
      PutObjectRequest putRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileKeyS3)
          .build();

      s3.putObject(putRequest, RequestBody.fromFile(localPath));
    
    } catch (RuntimeException e) {
      String mensagem = "Ocorreu um erro ao salvar as imagens.";
      procService.registrarErro(processamento, mensagem);

      throw new Exception(mensagem);
    }
  }
}
