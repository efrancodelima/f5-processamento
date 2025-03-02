package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.mapper.ProcessamentoMapper;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListarVideosService {

  // Atributos
  private final ProcessamentoRepository repository;
  private UsuarioJpa usuario = new UsuarioJpa(1L, "email@email.com");

  // Construtor
  @Autowired
  public ListarVideosService(ProcessamentoRepository repository) {
    this.repository = repository;
  }

  // Método público
  public List<ProcessamentoDto> execute() {
    var listaJpa = repository.findByUsuarioOrderByNumeroVideoDesc(usuario);
    return ProcessamentoMapper.toDto(listaJpa);
  }
}
