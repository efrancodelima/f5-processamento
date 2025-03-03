package br.com.fiap.soat.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomErrorResponse {
  private int statusCode;
  private String error;
  private String message;
  private String timestamp;
  private String path;
}