package br.com.fiap.soat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FalhaDto {
  private String jobId;
  private int errorCode;
  private String errorMessage;
}
