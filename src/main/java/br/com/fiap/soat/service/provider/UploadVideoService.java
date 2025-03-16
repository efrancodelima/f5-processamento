package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.service.util.ProcessarVideoService;
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
  private final ProcessarVideoService processarVideoService;

  // Construtores
  @Autowired
  public UploadVideoService(UsuarioService usuarioService,
      ProcessarVideoService processarVideoService) {
    this.usuarioService = usuarioService;
    this.processarVideoService = processarVideoService;
  }

  // Métodos públicos
  public void execute(HttpServletRequest requisicao, List<MultipartFile> videos) {

    UsuarioJpa usuario = usuarioService.getUsuario(requisicao);

    RemoverElementosNulos.remover(videos);
    List<FileWrapper> videosWrapper = encapsularRequisicao(videos);

    for (var video : videosWrapper) {
      processarVideoService.execute(video, usuario);
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
