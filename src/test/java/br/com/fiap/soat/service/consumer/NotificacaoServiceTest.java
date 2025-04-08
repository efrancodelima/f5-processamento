package br.com.fiap.soat.service.consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.exception.BadGatewayException;

class NotificacaoServiceTest {
  
  AutoCloseable closeable;

  @Captor
  ArgumentCaptor<String> stringCaptor;

  @Captor
  ArgumentCaptor<HttpEntity<EmailDto>> httpEntityCaptor;

  @Mock
  RestTemplate restTemplate;

  @InjectMocks
  NotificacaoService service;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveEnviarEmailComSucesso() throws BadGatewayException {
    // Arrange
    String emailDestino = "email@email.com";
    String texto = "texto";
    String assunto = "assunto";

    EmailDto dadosEmail = new EmailDto();
    dadosEmail.setEmailDestino(emailDestino);
    dadosEmail.setTexto(texto);
    dadosEmail.setAssunto(assunto);

    doReturn(null).when(restTemplate).exchange(
        Mockito.anyString(),
        Mockito.any(HttpMethod.class),
        Mockito.<HttpEntity<EmailDto>>any(),
        Mockito.<ParameterizedTypeReference<Void>>any());

    // Act
    service.enviarEmail(dadosEmail);

    // Assert
    verify(restTemplate).exchange(
        stringCaptor.capture(),
        Mockito.eq(HttpMethod.POST),
        httpEntityCaptor.capture(),
        Mockito.<ParameterizedTypeReference<Void>>any()
    );

    String url = stringCaptor.getValue();
    HttpEntity<EmailDto> dados = httpEntityCaptor.getValue();
    EmailDto dadosEmailCapturados = dados.getBody();

    assertTrue(url.contains("/email/enviar"));
    assertEquals(emailDestino, dadosEmailCapturados.getEmailDestino());
    assertEquals(assunto, dadosEmailCapturados.getAssunto());
    assertEquals(texto, dadosEmailCapturados.getTexto());
  }

  @Test
  void deveLancarExcecao() {
    // Arrange
    EmailDto dadosEmail = new EmailDto();
    
    doThrow(new RestClientException("")).when(restTemplate).exchange(
        Mockito.anyString(),
        Mockito.any(HttpMethod.class),
        Mockito.<HttpEntity<EmailDto>>any(),
        Mockito.<ParameterizedTypeReference<Void>>any());

    // Act and assert
    assertThrows(BadGatewayException.class, () -> service.enviarEmail(dadosEmail));
  }
}
