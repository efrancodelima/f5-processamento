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
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class ProcessarVideoService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  private static final int INTERVALO_CAPTURA = 15;
  private static final int DURACAO_LINK_MINUTOS = 24 * 60;
  
  private final ProcessamentoService procService;
  private final AwsConfig awsConfig;
  
  // Construtor
  @Autowired
  public ProcessarVideoService(ProcessamentoService procService, AwsConfig awsConfig) {
    this.procService = procService;
    this.awsConfig = awsConfig;
  }

  // Método público
  @Async
  public CompletableFuture<Boolean> execute(FileWrapper video, UsuarioJpa usuario) {

    String uniqueId = UUID.randomUUID().toString();
    String diretorioBase = TEMP_DIR + uniqueId;
    String diretorioImagens = diretorioBase + "/imagens";
    String caminhoArquivoZip = diretorioBase + "/imagens.zip";
    ProcessamentoJpa processamento = procService.registrarInicio(video, usuario);
    
    String objectKeyS3 = usuario.getId().toString() + "/" + processamento.getNumeroVideo()
        + "_" + video.getName();

    try {
      verificarConteudoVideo(video, processamento);

      File videoFile = salvarVideo(video, diretorioBase, processamento);
      
      extrairImagensVideo(videoFile, diretorioImagens, processamento);
      
      videoFile.delete();
      
      File arquivoZip = compactarImagens(diretorioImagens, caminhoArquivoZip, processamento);
      
      ApagarDiretorio.apagar(diretorioImagens);
      
      salvarNoBucketS3(objectKeyS3, Paths.get(caminhoArquivoZip), processamento);
      
      arquivoZip.delete();

      String linkDownload = gerarLinkParaDownload(objectKeyS3, processamento);
      
      procService.registrarConclusao(processamento, linkDownload);
      return CompletableFuture.completedFuture(true);

    } catch (Exception e) {
      return CompletableFuture.completedFuture(false);
    }
  }

  private void verificarConteudoVideo(FileWrapper video, ProcessamentoJpa processamento)
      throws Exception {
    
    if (video.getContent() == null || video.getContent().length == 0) {
      String mensagem = "Não foi possível ler o arquivo " + video.getName();
      procService.registrarErro(processamento, mensagem);
      throw new Exception(mensagem);
    }
  }

  private File salvarVideo(FileWrapper video, String diretorio, ProcessamentoJpa processamento)
      throws Exception {
    
    try {
      return SalvarArquivo.salvar(video, diretorio);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao salvar o arquivo.";
      procService.registrarErro(processamento, mensagem);
      throw e;
    }
  }

  private void extrairImagensVideo(File video, String diretorioImagens,
      ProcessamentoJpa processamento) throws Exception {

    try {
      ExtrairImagens.extrair(video, INTERVALO_CAPTURA, diretorioImagens);

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

  private File compactarImagens(String diretorioImagens, String caminhoArquivoZip,
      ProcessamentoJpa processamento) throws Exception {
    
    try {
      return CompactarArquivos.compactar(diretorioImagens, caminhoArquivoZip);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao compactar as imagens.";
      procService.registrarErro(processamento, mensagem);
      
      throw e;
    }
  }

  private void salvarNoBucketS3(String objectKey, Path localPath, ProcessamentoJpa processamento)
      throws Exception {

    try {
      S3Client s3 = S3Client.builder()
          .region(awsConfig.pegarRegiao())
          .credentialsProvider(StaticCredentialsProvider.create(awsConfig.pegarCredenciais()))
          .build();
      
      PutObjectRequest putRequest = PutObjectRequest.builder()
          .bucket(awsConfig.getBucketName())
          .key(objectKey)
          .build();

      s3.putObject(putRequest, RequestBody.fromFile(localPath));
    
    } catch (RuntimeException e) {
      String mensagem = "Ocorreu um erro ao salvar as imagens.";
      procService.registrarErro(processamento, mensagem);
      throw new Exception(mensagem);
    }
  }

  private String gerarLinkParaDownload(String objectKey, ProcessamentoJpa processamento)
      throws Exception {
    
    // Configura o AWS Presigner
    S3Presigner presigner = S3Presigner.builder()
        .region(awsConfig.pegarRegiao())
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
    
    try {
      // Cria a requisição
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .getObjectRequest(req -> req.bucket(awsConfig.getBucketName()).key(objectKey))
          .signatureDuration(Duration.ofMinutes(DURACAO_LINK_MINUTOS))
          .build();

      // Gera o link e retorna
      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    
    } catch (RuntimeException e) {
      String mensagem = "Ocorreu um erro ao gerar o link para download das imagens.";
      procService.registrarErro(processamento, mensagem);
      throw new Exception(mensagem);
    
    } finally {
      presigner.close();  
    }
  }
}
