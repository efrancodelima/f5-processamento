package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.util.LoggerAplicacao;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  public UsuarioJpa getUsuario(HttpServletRequest requisicao) {

    LoggerAplicacao.info("chegou no GETuSUARIO");

    // String nome = (String) requisicao.getAttribute("name");
    // String email = (String) requisicao.getAttribute("email");
    // String emissor = (String) requisicao.getAttribute("iss");

    // LoggerAplicacao.info(nome);
    // LoggerAplicacao.info(email);
    // LoggerAplicacao.info(emissor);

    return new UsuarioJpa();
  }
  
}
