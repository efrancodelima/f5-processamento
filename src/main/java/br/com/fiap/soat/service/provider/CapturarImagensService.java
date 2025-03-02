package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.util.ApagarDiretorio;
import br.com.fiap.soat.util.CompactarArquivos;
import br.com.fiap.soat.util.ExtrairImagens;
import br.com.fiap.soat.util.SalvarArquivo;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.io.File;
import java.time.LocalDateTime;
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

  private UsuarioJpa usuario = new UsuarioJpa(1L, "email@email.com");
  private final ProcessamentoRepository repository;

  // Construtor
  @Autowired
  public CapturarImagensService(ProcessamentoRepository repository) {
    this.repository = repository;
  }

  // Método público
  @Async
  public CompletableFuture<Void> execute(FileWrapper video) {

    String uniqueId = UUID.randomUUID().toString();
    String diretorioBaseStr = TEMP_DIR + uniqueId;
    String diretorioImagensStr = diretorioBaseStr + "/imagens";
    String caminhoArquivoZip = diretorioBaseStr + "/imagens.zip";
    ProcessamentoJpa processamento = registrarInicioProcessamento(video);
    String nomeVideo = video.getName();
    
    if (video.getContent().length == 0) {
      String mensagem = "Não foi possível ler o arquivo " + nomeVideo;
      registrarErroProcessamento(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }

    // Converte o vídeo de MultipartFile para File
    File videoFile;
    try {
      videoFile = SalvarArquivo.salvar(video, diretorioBaseStr);
    } catch (Exception e) {
      String mensagem = "Ocorreu um erro ao salvar o arquivo."
          + ". Por favor, contate o suporte técnico.";
      registrarErroProcessamento(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }

    // Extrai as imagens do vídeo
    try {
      ExtrairImagens.extrair(videoFile, INTERVALO, diretorioImagensStr);
    } catch (Exception e) {
      String mensagem;

      if (e.getMessage().contains("Could not open input")) {
        mensagem = "O arquivo não é compatível com este serviço.";
      } else {
        mensagem = "Ocorreu um erro ao extrair as imagens do vídeo" 
            + ". Por favor, contate o suporte técnico.";
      }
      registrarErroProcessamento(processamento, mensagem);
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
      registrarErroProcessamento(processamento, mensagem);
      return CompletableFuture.completedFuture(null);
    }

    // Apaga o diretório das imagens
    ApagarDiretorio.apagar(diretorioImagensStr);

    // // Faz o upload do arquivo para o S3 da AWS
    // // É um URL pré-assinado que tem data de validade


    // // Encerra
    registrarSucessoProcessamento(processamento, "https://example.com/");
    return CompletableFuture.completedFuture(null);
  }

  // Métodos privados
  private ProcessamentoJpa registrarInicioProcessamento(FileWrapper video) {

    var processamento = ProcessamentoJpa.builder()
        .nomeVideo(video.getName())
        .usuario(usuario)
        .statusProcessamento(StatusProcessamento.PENDENTE)
        .timestampInicio(LocalDateTime.now())
        .build();

    return repository.save(processamento);
  }

  private void registrarErroProcessamento(ProcessamentoJpa processamento, String mensagemErro) {
    processamento.setStatusProcessamento(StatusProcessamento.ERRO);
    processamento.setMensagemErro(mensagemErro);
    processamento.setTimestampConclusao(LocalDateTime.now());
    repository.save(processamento);
  }
  
  private void registrarSucessoProcessamento(ProcessamentoJpa processamento, String linkArquivo) {
    processamento.setStatusProcessamento(StatusProcessamento.SUCESSO);
    processamento.setLinkDownload(linkArquivo);
    processamento.setTimestampConclusao(LocalDateTime.now());
    repository.save(processamento);
  }
}
