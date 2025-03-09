package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.mapper.ProcessamentoMapper;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.service.util.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListarVideosService {

  // Atributos
  private final ProcessamentoRepository repository;
  private final UsuarioService usuarioService;
  
  // Construtor
  @Autowired
  public ListarVideosService(ProcessamentoRepository repository, UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
    this.repository = repository;
  }

  // Método público
  public List<ProcessamentoDto> execute(HttpServletRequest requisicao) {

    UsuarioJpa usuario = usuarioService.getUsuario(requisicao);

    var listaJpa = repository.findByUsuarioOrderByNumeroVideoDesc(usuario);
    ordenarLista(listaJpa);

    return ProcessamentoMapper.toDto(listaJpa);
  }

  private void ordenarLista(List<ProcessamentoJpa> lista) {
    lista.sort(Comparator
        .comparing((ProcessamentoJpa p) -> !StatusProcessamento.PENDENTE
            .equals(p.getStatusProcessamento()))
        .thenComparing(p -> p.getTimestampInicio(), Comparator.reverseOrder())
    );
  }
}
