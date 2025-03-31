package br.com.fiap.soat.service.consumer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.soat.constants.Constantes;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.BadGatewayMessage;

@Service
public class GoogleCertsService {
  
  public Map<String, String> getGoogleCertificates()
      throws BadGatewayException, MalformedURLException {

    URL urlCertificado = new URL(Constantes.URL_GOOGLE_CERTS);

    try (InputStream is = urlCertificado.openStream();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(is, StandardCharsets.UTF_8))) {

      String jsonResponse = reader.lines().collect(Collectors.joining());
      
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(jsonResponse, Map.class);

    } catch (Exception e) {
      throw new BadGatewayException(BadGatewayMessage.googleCerts);
    }
  }
}
