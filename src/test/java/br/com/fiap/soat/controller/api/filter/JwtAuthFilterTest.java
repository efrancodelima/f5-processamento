package br.com.fiap.soat.controller.api.filter;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
class JwtAuthFilterTest {

  String endpoint = "/video/listar";

  @LocalServerPort
  private int port;

  @BeforeEach
  void setup() throws Exception {
    RestAssured.port = port;
  }

  @Test
  void deveRejeitarTokenExpirado() {
    Response response = given()
        .header("Authorization", "Bearer " + JwtAuthFilterMock.getExpiredToken())
        .when()
          .get(endpoint)
        .then()
          .extract().response();

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
  }
}
