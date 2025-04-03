package br.com.fiap.soat.exception.messages;

public enum AuthMessage {
  
  tokenInvalido("Token inválido."),
  cabecalhoNulo("Token inválido (cabeçalho nulo)."),
  kidAusente("Token inválido (kid ausente)."),
  certNaoEncontrado("Token inválido (certificado não encontrado)."),
  emissorInvalido("Token inválido (emissor inválido).");

  private String mensagem;

  AuthMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
