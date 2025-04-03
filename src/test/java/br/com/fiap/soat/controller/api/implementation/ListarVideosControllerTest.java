package br.com.fiap.soat.controller.api.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.soat.controller.api.filter.JwtAuthFilter;
import br.com.fiap.soat.controller.api.filter.JwtAuthFilterMock;
import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.service.provider.ListarVideosService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(ListarVideosController.class)
@ContextConfiguration(classes = { ListarVideosController.class, ListarVideosService.class })
class ListarVideosControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtAuthFilter authFilter;

  @MockitoBean
  private ListarVideosService listarService;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);
    mockAuthFilter();
    mockListarService();
  }

  @Test
  public void testShowHome() throws Exception {

    MvcResult mvcResult = this.mockMvc
        .perform(
          get("/video/listar")
          .header("Authorization", "Bearer xxxxx"))
        .andReturn();

    int statusCode = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    List<ProcessamentoDto> lista = (new ObjectMapper())
        .readValue(content, new TypeReference<List<ProcessamentoDto>>() {});

    assertEquals(HttpStatus.OK.value(), statusCode);
    assertEquals(1, lista.size());
  }

  // Métodos privados
  private void mockAuthFilter() throws Exception {
    var filterMock = new JwtAuthFilterMock();
    doAnswer(invocation -> {
      HttpServletRequest request = invocation.getArgument(0);
      HttpServletResponse response = invocation.getArgument(1);
      FilterChain chain = invocation.getArgument(2);
      filterMock.doFilter(request, response, chain);
      return null;
    }).when(authFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());
  }

  private void mockListarService() {
    List<ProcessamentoDto> lista = new ArrayList<>();
    lista.add(new ProcessamentoDto());
    doReturn(lista).when(listarService).listar(Mockito.any());
  }
}
