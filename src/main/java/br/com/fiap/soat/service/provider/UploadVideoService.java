package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.service.util.CapturarImagensService;
import br.com.fiap.soat.service.util.UsuarioService;
import br.com.fiap.soat.util.RemoverElementosNulos;
import br.com.fiap.soat.wrapper.FileWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadVideoService {

  // Atributos
  private final UsuarioService usuarioService;
  private final CapturarImagensService capturarImagensService;

  // Construtores
  @Autowired
  public UploadVideoService(UsuarioService usuarioService,
      CapturarImagensService capturarImagensService) {
    this.usuarioService = usuarioService;
    this.capturarImagensService = capturarImagensService;
  }

  // Métodos públicos
  public void execute(HttpServletRequest requisicao, List<MultipartFile> videos) {

    UsuarioJpa usuario = usuarioService.getUsuario(requisicao);

    RemoverElementosNulos.remover(videos);
    List<FileWrapper> videosWrapper = encapsularRequisicao(videos);

    for (var video : videosWrapper) {
      capturarImagensService.execute(video, usuario); // método assíncrono
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
