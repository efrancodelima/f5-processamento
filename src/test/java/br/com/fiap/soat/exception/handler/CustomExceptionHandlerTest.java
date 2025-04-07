package br.com.fiap.soat.exception.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.AuthException;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import br.com.fiap.soat.exception.messages.AuthMessage;
import br.com.fiap.soat.exception.messages.BadGatewayMessage;

class CustomExceptionHandlerTest {

  AutoCloseable closeable;

  @Mock
  WebRequest request;

  @InjectMocks
  CustomExceptionHandler handler;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  void deveTratarBadGatewayException() {
    // Arrange
    var excecao = new BadGatewayException(BadGatewayMessage.googleCerts);
    String path = "/video/upload";
    doReturn("URI=" + path).when(request).getDescription(Mockito.anyBoolean());

    // Act
    ResponseEntity<CustomErrorResponse> resposta = 
        handler.handleResponseStatusException(excecao, request);

    // Assert
    assertEquals(HttpStatus.BAD_GATEWAY.value(), resposta.getStatusCode().value());
    
    CustomErrorResponse errorResponse = resposta.getBody();

    assertEquals(HttpStatus.BAD_GATEWAY.getReasonPhrase(), errorResponse.getError());
    assertEquals(BadGatewayMessage.googleCerts.getMessage(), errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
    assertEquals(path, errorResponse.getPath());
  }
  
  @Test
  void deveTratarApplicationException() {
    // Arrange
    var excecao = new ApplicationException(ApplicationMessage.criarJob);
    String path = "/video/upload";
    doReturn("URI=" + path).when(request).getDescription(Mockito.anyBoolean());

    // Act
    ResponseEntity<CustomErrorResponse> resposta = 
        handler.handleResponseStatusException(excecao, request);

    // Assert
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), resposta.getStatusCode().value());
    
    CustomErrorResponse errorResponse = resposta.getBody();

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(), errorResponse.getError());
    assertEquals(ApplicationMessage.criarJob.getMessage(), errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
    assertEquals(path, errorResponse.getPath());
  }
  
  @Test
  void deveTratarAuthException() {
    // Arrange
    var excecao = new AuthException(AuthMessage.tokenInvalido);
    String path = "/video/upload";
    doReturn("URI=" + path).when(request).getDescription(Mockito.anyBoolean());

    // Act
    ResponseEntity<CustomErrorResponse> resposta = 
        handler.handleResponseStatusException(excecao, request);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED.value(), resposta.getStatusCode().value());
    
    CustomErrorResponse errorResponse = resposta.getBody();

    assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), errorResponse.getError());
    assertEquals(AuthMessage.tokenInvalido.getMessage(), errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
    assertEquals(path, errorResponse.getPath());
  }
}
