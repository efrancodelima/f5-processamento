package br.com.fiap.soat.service.consumer;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.BadGatewayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
    
    String url = baseUrl + "/email/enviar";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<EmailDto> httpEntity = new HttpEntity<>(dadosEmail, headers);

    try {
      restTemplate.exchange(
          url,
          HttpMethod.POST,
          httpEntity,
          new ParameterizedTypeReference<Void>() {});

    } catch (Exception e) {
      throw new BadGatewayException(BadGatewayMessage.notificacao);
    }
  }
}