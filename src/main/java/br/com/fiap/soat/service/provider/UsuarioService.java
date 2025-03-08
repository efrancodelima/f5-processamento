package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.util.LoggerAplicacao;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  public UsuarioJpa getUsuario(HttpServletRequest requisicao) {

    Claims claims = (Claims) requisicao.getAttribute("claims");

    String nome = (String) claims.get("name");
    String email = (String) claims.get("email");
    String emissor = (String) claims.get("iss");

    LoggerAplicacao.info(nome);
    LoggerAplicacao.info(email);
    LoggerAplicacao.info(emissor);

    var usuario = new UsuarioJpa();
    usuario.setNome(nome);
    usuario.setEmail(email);
    
    return usuario;
  }
  
}
