package br.com.fiap.soat.exception.messages;

public enum BadGatewayMessage {
    
  NOTIFICACAO("Erro na comunicação com o microsserviço de notificação."),
  GOOGLE_CERTS("Erro na comunicação com o serviço de certificados do Google.");

  private String mensagem;

  BadGatewayMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
