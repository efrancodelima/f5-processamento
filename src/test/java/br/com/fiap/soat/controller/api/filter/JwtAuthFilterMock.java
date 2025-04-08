package br.com.fiap.soat.controller.api.filter;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilterMock {

  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    Claims claims = new DefaultClaims();
    claims.put("name", "User Test");
    claims.put("email", "email@email.com");
    request.setAttribute("claims", claims);
    chain.doFilter(request, response);
  }
}
