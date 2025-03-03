package br.com.fiap.soat.service.util;

import br.com.fiap.soat.entity.ProcessamentoJpa;
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
public class CapturarImagensService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  private static final int INTERVALO = 15;
  private final ProcessamentoService procService;

  // Construtor
  @Autowired
  public CapturarImagensService(ProcessamentoService procService) {
    this.procService = procService;
  }

  // Método público
  @Async
  public CompletableFuture<Void> execute(FileWrapper video) {

    String uniqueId = UUID.randomUUID().toString();
    String diretorioBaseStr = TEMP_DIR + uniqueId;
    String diretorioImagensStr = diretorioBaseStr + "/imagens";
    String caminhoArquivoZip = diretorioBaseStr + "/imagens.zip";
    ProcessamentoJpa processamento = procService.registrarInicio(video);
    String nomeVideo = video.getName();
    
    if (video.getContent().length == 0) {
      String mensagem = "Não foi possível ler o arquivo " + nomeVideo;
      procService.registrarErro(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }

    // Converte o vídeo de MultipartFile para File
    File videoFile;
    try {
      videoFile = SalvarArquivo.salvar(video, diretorioBaseStr);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao salvar o arquivo."
          + ". Por favor, contate o suporte técnico.";
      procService.registrarErro(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }

    // Extrai as imagens do vídeo
    try {
      ExtrairImagens.extrair(videoFile, INTERVALO, diretorioImagensStr);
    } catch (Exception e) {
      String mensagem;

      if (e.getMessage().contains("Could not open input")) {
        mensagem = "O tipo de arquivo enviado não é compatível com este serviço.";
      } else {
        mensagem = "Ocorreu um erro ao extrair as imagens do vídeo" 
            + ". Por favor, contate o suporte técnico.";
      }
      procService.registrarErro(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }
    
    // Apaga o vídeo recebido
    videoFile.delete();

    // Compacta as imagens em um arquivo zip
    try {
      CompactarArquivos.compactar(diretorioImagensStr, caminhoArquivoZip);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao compactar as imagens. "
          + "Por favor, contate o suporte técnico.";
      procService.registrarErro(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }

    // Apaga o diretório das imagens
    ApagarDiretorio.apagar(diretorioImagensStr);

    // // Faz o upload do arquivo para o S3 da AWS
    // // É um URL pré-assinado que tem data de validade


    // // Encerra
    procService.registrarConclusao(processamento, "https://example.com/");
    return CompletableFuture.completedFuture(null);
  }
}
