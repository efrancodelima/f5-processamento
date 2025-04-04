package br.com.fiap.soat.service.other;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  // Atributos
  private final UsuarioRepository usuarioRepository;

  // Construtores
  @Autowired
  public UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  // Métodos públicos
  public UsuarioJpa getUsuario(HttpServletRequest requisicao) {

    Claims claims = (Claims) requisicao.getAttribute("claims");
    
    String nome = (String) claims.get("name");
    String email = (String) claims.get("email");
    
    var usuarioOpt = usuarioRepository.findByEmail(email);

    if (usuarioOpt.isPresent()) {
      return usuarioOpt.get();
    }

    var novoUsuario = new UsuarioJpa(null, nome, email);
    return usuarioRepository.save(novoUsuario);
  }
}
