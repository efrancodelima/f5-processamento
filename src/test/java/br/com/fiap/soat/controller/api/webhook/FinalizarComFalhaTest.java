package br.com.fiap.soat.controller.api.webhook;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.soat.dto.FalhaDto;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FinalizarComFalhaTest {

  String endpoint = "/video/falha";

  @LocalServerPort
  private int port;

  @BeforeEach
  void setup() {
    RestAssured.port = port;
  }

  @Test
  void testEndpoint() throws JsonProcessingException {
    given()
      .contentType("application/json")
      .body(getRequisicao())
      .when()
        .patch(endpoint)
      .then()
        .statusCode(204);
  }

  private String getRequisicao() throws JsonProcessingException {
    FalhaDto requisicao = new FalhaDto("jobId", 1010, "errorMessage");
    return (new ObjectMapper()).writeValueAsString(requisicao);
  }
}
