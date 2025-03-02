package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.util.RemoverElementosNulos;
import br.com.fiap.soat.wrapper.FileWrapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadVideoService {

  // Atributos
  private final CapturarImagensService capturarImagensService;

  // Construtores
  @Autowired
  public UploadVideoService(CapturarImagensService capturarImagensService) {
    this.capturarImagensService = capturarImagensService;
  }

  // Métodos públicos
  public void execute(List<MultipartFile> videos) {

    RemoverElementosNulos.remover(videos);
    List<FileWrapper> videosWrapper = encapsularRequisicao(videos);

    for (var video : videosWrapper) {
      capturarImagensService.execute(video); // método assíncrono
    }
  }

  // Métodos privados
  private List<FileWrapper> encapsularRequisicao(List<MultipartFile> videos) {
    var resposta = new ArrayList<FileWrapper>();
    for (var video : videos) {
      resposta.add(new FileWrapper(video));
    }
    return resposta;
  }
}
