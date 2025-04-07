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

  public static String getExpiredToken() {
    return "eyJhbGciOiJSUzI1NiIsImtpZCI6IjcxMTE1MjM1YTZjNjE0NTRlZmRlZGM0NWE3N2U0Mz"
        + "UxMzY3ZWViZTAiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiRWRlcnNvbiIsInBpY3R1cmUiOiJ"
        + "odHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NJNEgwSUtRYkx4UFN"
        + "RWHpwU1lIZ2hES2NZSGxUWXlLMzZhY0EyZkJiMzVfdjJUR1E9czk2LWMiLCJpc3MiOiJodHR"
        + "wczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vc29hdC1mYXNlLTUiLCJhdWQiOiJzb2F0LWZh"
        + "c2UtNSIsImF1dGhfdGltZSI6MTc0NDA0Njc5MCwidXNlcl9pZCI6ImtOUXJHWEZFYVZTT2NxO"
        + "EJHeFlSRElrV2I5aDEiLCJzdWIiOiJrTlFyR1hGRWFWU09jcThCR3hZUkRJa1diOWgxIiwiaW"
        + "F0IjoxNzQ0MDQ2NzkwLCJleHAiOjE3NDQwNTAzOTAsImVtYWlsIjoiZWRlcnNvbi5yZWNpZmV"
        + "AZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImZpcmViYXNlIjp7ImlkZW50aXRp"
        + "ZXMiOnsiZ29vZ2xlLmNvbSI6WyIxMDk5MTQxODQxMDI2NjA2MDA4NjAiXSwiZW1haWwiOlsiZ"
        + "WRlcnNvbi5yZWNpZmVAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLm"
        + "NvbSJ9fQ.aD0y1Uw3PQpd1kuWq-a1Igm9_OqEs8qd07Q29oDIxVMb15aFbr98kFhd_dV_r3Rc"
        + "K0Grn05JbP7vsvUlk7i8TGczZfstuOaqkD7ddcnzKTGh82WT4mQ7D1zFLPC35x-34Q04ElIoB"
        + "IHRTvLoHvS32SfyVSEG9Ac2FRvdCTmjWh2dzhUyX4Ay4llLZRyihXfl1SzbyyQP4QIaADFbDAJ"
        + "-PpTFel4risL-BYBQiw1BCsF4WlavfLNLmbfNd2VfSBv10a4OtensNNBoikC82zpmmlRtcq1DN"
        + "_2SD0Ltzq3T7-gxUjzXar2-3hoR9e4DCsBLATObCWDJRaQVHCtDKhAaaQ";
  }
}
