package br.com.fiap.soat.controller.api.implementation;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.fiap.soat.controller.api.filter.JwtAuthFilter;
import br.com.fiap.soat.controller.api.filter.JwtAuthFilterMock;
import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.entity.StatusProcessamento;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
class ListarVideosControllerIT {

  String endpoint = "/video/listar";

  @MockitoBean
  private JwtAuthFilter authFilter;

  @LocalServerPort
  private int port;

  @BeforeEach
  void setup() throws Exception {
    RestAssured.port = port;
    mockAuthFilter();
  }

  @Test
  @Sql(scripts = "/user-1-data.sql")
  void deveListarOsVideosDoUsuario() throws Exception {
    // Act
    Response response = given()
        .contentType("application/json")
        .header("Authorization", "Bearer token")
        .when()
          .get(endpoint)
        .then()
          .extract().response();

    // Assert
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());

    List<ProcessamentoDto> responseBody = 
        response.getBody().as(new TypeRef<List<ProcessamentoDto>>() {});

    assertEquals(3, responseBody.size());
    assertEquals("video_03.mp4", responseBody.get(0).getNomeArquivo());
    assertEquals(StatusProcessamento.PROCESSANDO.getMessage(), 
        responseBody.get(0).getStatusProcessamento());
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
}
