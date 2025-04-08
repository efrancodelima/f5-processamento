package br.com.fiap.soat.service.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.StatusProcessamento;
import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.repository.ProcessamentoRepository;
import br.com.fiap.soat.service.other.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

class ListarVideosServiceTest {
  
  AutoCloseable closeable;

  @Mock
  ProcessamentoRepository repository;

  @Mock
  UsuarioService usuarioService;

  @Mock
  HttpServletRequest requisicao;

  @InjectMocks
  ListarVideosService service;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveListarEOrdenarOsVideosComSucesso() {

    // Arrange
    UsuarioJpa usuario = new UsuarioJpa();
    List<ProcessamentoJpa> lista = getLista();

    doReturn(usuario).when(usuarioService).getUsuario(requisicao);
    doReturn(lista).when(repository).findByUsuarioOrderByNumeroVideoDesc(usuario);

    // Act
    List<ProcessamentoDto> resposta = service.listar(requisicao);

    // Assert
    assertEquals(2, resposta.size());
    assertEquals(StatusProcessamento.RECEBIDO.getMessage(),
        resposta.get(0).getStatusProcessamento());
  }

  private List<ProcessamentoJpa> getLista() {
    var lista = new ArrayList<ProcessamentoJpa>();
    lista.add(getProcessamento(StatusProcessamento.PROCESSANDO));
    lista.add(getProcessamento(StatusProcessamento.RECEBIDO));
    return lista;
  }

  private ProcessamentoJpa getProcessamento(StatusProcessamento status) {
    return ProcessamentoJpa.builder()
            .statusProcessamento(status)
            .timestampInicio(LocalDateTime.now())
            .build();
  }
}
