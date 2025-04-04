package br.com.fiap.soat.service.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.repository.UsuarioRepository;
import br.com.fiap.soat.service.other.UsuarioService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;

class UsuarioServiceTest {

  AutoCloseable closeable;

  @Mock
  UsuarioRepository repository;

  @Mock
  UsuarioJpa usuario;

  @Mock
  HttpServletRequest requisicao;

  @InjectMocks
  UsuarioService service;
  
  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveRecuperarUsuarioComSucesso() {
    // Arrange
    doReturn(getClaims()).when(requisicao).getAttribute("claims");
    doReturn(Optional.of(usuario)).when(repository).findByEmail(Mockito.anyString());
    
    // Act
    UsuarioJpa response = service.getUsuario(requisicao);

    // Assert
    assertNotNull(response);
    assertEquals(usuario, response);
  }

  @Test
  void deveCadastrarUsuarioQuandoUsuarioNaoExistir() {
    // Arrange
    doReturn(getClaims()).when(requisicao).getAttribute("claims");
    doReturn(Optional.empty()).when(repository).findByEmail(Mockito.anyString());
    doReturn(usuario).when(repository).save(Mockito.any());
    
    // Act
    UsuarioJpa response = service.getUsuario(requisicao);

    // Assert
    assertNotNull(response);
    assertEquals(usuario, response);
    verify(repository).save(Mockito.any());
  }

  // Métodos privados
  private Claims getClaims() {
    Claims claims = new DefaultClaims();
    claims.put("name", "Nome do usuário");
    claims.put("email", "email@email.com");
    return claims;
  }
}
