package br.com.fiap.soat.service.provider;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.mapper.ProcessamentoMapper;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.service.other.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

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
  public List<ProcessamentoDto> listar(HttpServletRequest requisicao) {

    UsuarioJpa usuario = usuarioService.getUsuario(requisicao);

    List<ProcessamentoJpa> listaJpa = repository.findByUsuarioOrderByIdDesc(usuario);
    
    ordenarLista(listaJpa);

    return ProcessamentoMapper.toDto(listaJpa);
  }

  private void ordenarLista(List<ProcessamentoJpa> lista) {
    lista.sort(Comparator
        .comparing((ProcessamentoJpa p) -> {
          if (p.getStatus() == StatusProcessamento.RECEBIDO) {
            return 1;
          } else if (p.getStatus() == StatusProcessamento.PROCESSANDO) {
            return 2;
          } else {
            return 3;
          }
        })
        .thenComparing(ProcessamentoJpa::getTimestampInicio, Comparator.reverseOrder()));
  }
}
