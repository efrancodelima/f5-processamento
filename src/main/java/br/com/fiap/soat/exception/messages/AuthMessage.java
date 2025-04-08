package br.com.fiap.soat.exception.messages;

public enum AuthMessage {
  
  TOKEN_INVALIDO("Token inválido."),
  CABECALHO_NULO("Token inválido (cabeçalho nulo)."),
  KID_AUSENTE("Token inválido (kid ausente)."),
  CERT_NOT_FOUND("Token inválido (certificado não encontrado)."),
  EMISSOR_INVALIDO("Token inválido (emissor inválido).");

  private String mensagem;

  AuthMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
