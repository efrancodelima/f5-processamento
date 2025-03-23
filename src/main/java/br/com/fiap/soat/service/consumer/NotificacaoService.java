package br.com.fiap.soat.service.consumer;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.BadGatewayMessage;
import br.com.fiap.soat.util.LoggerAplicacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificacaoService {

  private final RestTemplate restTemplate;
  private final String baseUrl;
    
  @Autowired
  private NotificacaoService(RestTemplate restTemplate,
      @Value("${notificacao.service.url}") String baseUrl) {
    
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }
  
  public void enviarEmail(EmailDto dadosEmail) throws BadGatewayException {
    
    String url = baseUrl + "email/enviar/";

    LoggerAplicacao.info("URL: " + url);
    LoggerAplicacao.info("Dados do email: " + dadosEmail);


    try {
      restTemplate.exchange(
          url,
          HttpMethod.POST,
          new HttpEntity<>(dadosEmail),
          new ParameterizedTypeReference<Void>() {});

    } catch (Exception e) {
      LoggerAplicacao.error(e.getMessage());
      throw new BadGatewayException(BadGatewayMessage.notificacao);
    }
  }
}