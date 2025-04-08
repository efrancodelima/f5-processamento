package br.com.fiap.soat.controller.webhook;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.soat.controller.api.filter.JwtAuthFilter;
import br.com.fiap.soat.controller.api.filter.JwtAuthFilterMock;
import br.com.fiap.soat.dto.FalhaDto;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.service.provider.FinalizarComFalhaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(FalhaController.class)
@ContextConfiguration(classes = { FalhaController.class, FinalizarComFalhaService.class })
class FalhaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtAuthFilter authFilter;

  @MockitoBean
  private FinalizarComFalhaService falhaService;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);
    mockAuthFilter();
    mockFalhaService();
  }

  @Test
  void testShowHome() throws Exception {
    this.mockMvc.perform(patch("/video/falha")
          .contentType(MediaType.APPLICATION_JSON)
          .content(getRequestContent()))
        .andExpect(status().isNoContent());
  }

  // Métodos privados
  private String getRequestContent() throws JsonProcessingException {
    FalhaDto requisicao = new FalhaDto("jobId", 1010, "errorMessage");
    return (new ObjectMapper()).writeValueAsString(requisicao);
  }

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

  private void mockFalhaService() throws BadGatewayException {
    doReturn(null).when(falhaService).finalizar(Mockito.any());
  }
}
