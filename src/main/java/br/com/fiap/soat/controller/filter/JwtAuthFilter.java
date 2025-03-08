package br.com.fiap.soat.controller.filter;

import br.com.fiap.soat.constants.Constantes;
import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import br.com.fiap.soat.exception.messages.BadGatewayMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthFilter extends HttpFilter {
  
  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    // Se método OPTIONS, retorna
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      setResponseOptions(request, response);
      return;
    }

    // Pega o token codificado
    String token = getToken(request);
    if (token == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso não autorizado.");
      return;
    }
    
    // Decodifica o token
    try {
      Map<String, Object> header = getTokenHeader(token);
      String kid = getKeyKid(header);
      PublicKey publicKey = getPublicKey(kid);
      Claims claims = getTokenClaims(token, publicKey);
      checkTokenIssuer(claims);
      request.setAttribute("claims", claims);

    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso não autorizado.");
      return;
    }

    chain.doFilter(request, response);
  }

  private void setResponseOptions(HttpServletRequest request, HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private String getToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");

    if (bearer == null || !bearer.startsWith("Bearer ")) {
      return null;
    } else {
      return bearer.substring(7);
      // A substring é para remover o prefixo "Bearer "
    }
  }

  private Map<String, Object> getTokenHeader(String token) throws ApplicationException {

    String[] parts = token.split("\\.");
    
    if (parts.length < 2) {
      throw new ApplicationException(ApplicationMessage.tokenInvalido);
    }

    String headerJson = new String(Base64.getUrlDecoder()
        .decode(parts[0]), StandardCharsets.UTF_8);
    
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> headerMap = objectMapper.readValue(headerJson, Map.class);
      return headerMap;

    } catch (Exception e) {
      throw new ApplicationException(ApplicationMessage.tokenInvalido);
    }
  }

  private String getKeyKid(Map<String, Object> header) throws ApplicationException {
    if (header == null) {
      throw new ApplicationException(ApplicationMessage.cabecalhoNulo);
    }

    String kid = (String) header.get("kid");
    if (kid == null) {
      throw new ApplicationException(ApplicationMessage.kidAusente);
    }

    return kid;
  }
  
  private PublicKey getPublicKey(String kid)
      throws ApplicationException, BadGatewayException, MalformedURLException {
    
    X509Certificate certificado;
    Map<String, String> certificados = getGoogleCertificates();

    // Seleciona o certificado correspondente ao kid
    String certificadoPem = certificados.get(kid);
    if (certificadoPem == null) {
      throw new ApplicationException(ApplicationMessage.certNaoEncontrado);
    }

    // Remove os delimitadores PEM
    String pem = certificadoPem.replace("-----BEGIN CERTIFICATE-----", "")
        .replace("-----END CERTIFICATE-----", "")
        .replaceAll("\\s+", "");

    // Decodifica o conteúdo
    byte[] conteudo = Base64.getDecoder().decode(pem);

    // Gera o certificado X.509 a partir do conteúdo
    try {
      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
      certificado = (X509Certificate) certFactory
          .generateCertificate(new ByteArrayInputStream(conteudo));
    
    } catch (Exception e) {
      throw new ApplicationException(ApplicationMessage.tokenInvalido);
    }

    return certificado.getPublicKey();
  }

  private Map<String, String> getGoogleCertificates()
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

  private Claims getTokenClaims(String token, PublicKey publicKey) {
    return Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private void checkTokenIssuer(Claims claims) throws ApplicationException {
    
    var emissor = claims.get("iss");
    
    if (emissor != Constantes.EMISSOR_CERTIFICADO) {
      throw new ApplicationException(ApplicationMessage.emissorInvalido);
    }
  }
}
