package br.com.fiap.soat.controller.api.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.soat.constants.Constantes;
import br.com.fiap.soat.exception.AuthException;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.messages.AuthMessage;
import br.com.fiap.soat.service.consumer.GoogleCertsService;
import br.com.fiap.soat.util.LoggerAplicacao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends HttpFilter {

  private final GoogleCertsService googleCerts;

  @Autowired
  public JwtAuthFilter(GoogleCertsService googleCerts) {
    this.googleCerts = googleCerts;
  }
  
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
      LoggerAplicacao.error(e.getMessage());
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

  private Map<String, Object> getTokenHeader(String token) throws AuthException {

    String[] parts = token.split("\\.");
    
    if (parts.length < 2) {
      throw new AuthException(AuthMessage.tokenInvalido);
    }

    String headerJson = new String(Base64.getUrlDecoder()
        .decode(parts[0]), StandardCharsets.UTF_8);
    
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> headerMap = objectMapper.readValue(headerJson, Map.class);
      return headerMap;

    } catch (Exception e) {
      throw new AuthException(AuthMessage.tokenInvalido);
    }
  }

  private String getKeyKid(Map<String, Object> header) throws AuthException {
    if (header == null) {
      throw new AuthException(AuthMessage.cabecalhoNulo);
    }

    String kid = (String) header.get("kid");
    if (kid == null) {
      throw new AuthException(AuthMessage.kidAusente);
    }

    return kid;
  }
  
  private PublicKey getPublicKey(String kid)
      throws AuthException, BadGatewayException, MalformedURLException {
    
    X509Certificate certificado;
    Map<String, String> certificados = googleCerts.getGoogleCertificates();
    LoggerAplicacao.error(certificados.toString());

    // Seleciona o certificado correspondente ao kid
    String certificadoPem = certificados.get(kid);
    if (certificadoPem == null) {
      throw new AuthException(AuthMessage.certNaoEncontrado);
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
      throw new AuthException(AuthMessage.tokenInvalido);
    }

    return certificado.getPublicKey();
  }

  private Claims getTokenClaims(String token, PublicKey publicKey) {
    return Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private void checkTokenIssuer(Claims claims) throws AuthException {
    
    String emissor = (String) claims.get("iss");
    
    if (!emissor.equals(Constantes.EMISSOR_CERTIFICADO)) {
      throw new AuthException(AuthMessage.emissorInvalido);
    }
  }
}
