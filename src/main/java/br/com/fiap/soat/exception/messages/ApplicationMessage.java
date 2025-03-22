package br.com.fiap.soat.exception.messages;

public enum ApplicationMessage {
    
  erroProcessamento("Ocorreu um erro inesperado ao processar o vídeo. "
        + "Por favor, contate o suporte técnico."),
  tokenInvalido("Token inválido."),
  cabecalhoNulo("Token inválido (cabeçalho nulo)."),
  kidAusente("Token inválido (kid ausente)."),
  certNaoEncontrado("Token inválido (certificado não encontrado)."),
  emissorInvalido("Token inválido (emissor inválido).");


  private String mensagem;

  ApplicationMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
