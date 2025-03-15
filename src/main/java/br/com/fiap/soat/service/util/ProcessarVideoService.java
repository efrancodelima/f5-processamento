package br.com.fiap.soat.service.util;

import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.util.ApagarDiretorio;
import br.com.fiap.soat.util.CompactarArquivos;
import br.com.fiap.soat.util.ExtrairImagens;
import br.com.fiap.soat.util.SalvarArquivo;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ProcessarVideoService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  private static final int INTERVALO = 15;
  private final ProcessamentoService procService;
  ProcessamentoJpa processamento;

  // Construtor
  @Autowired
  public ProcessarVideoService(ProcessamentoService procService) {
    this.procService = procService;
  }

  // Método público
  @Async
  public CompletableFuture<Boolean> execute(FileWrapper video, UsuarioJpa usuario) {

    String uniqueId = UUID.randomUUID().toString();
    String diretorioBaseStr = TEMP_DIR + uniqueId;
    String diretorioImagensStr = diretorioBaseStr + "/imagens";
    String caminhoArquivoZip = diretorioBaseStr + "/imagens.zip";
    processamento = procService.registrarInicio(video, usuario);
    
    try {
      verificarConteudoVideo(video);
      File videoFile = salvarVideo(video, diretorioBaseStr);
      extrairImagensVideo(videoFile, diretorioImagensStr);
      videoFile.delete();
      compactarImagens(diretorioImagensStr, caminhoArquivoZip);
      ApagarDiretorio.apagar(diretorioImagensStr);

      // Faz o upload do arquivo para o S3 da AWS
      // É um URL pré-assinado que tem data de validade

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
      String mensagem = "Ocorreu um erro ao salvar o arquivo."
          + ". Por favor, contate o suporte técnico.";
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
        mensagem = "Ocorreu um erro ao extrair as imagens do vídeo" 
            + ". Por favor, contate o suporte técnico.";
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
      String mensagem = "Ocorreu um erro ao compactar as imagens. "
          + "Por favor, contate o suporte técnico.";
      procService.registrarErro(processamento, mensagem);
      
      throw e;
    }
  }
}
