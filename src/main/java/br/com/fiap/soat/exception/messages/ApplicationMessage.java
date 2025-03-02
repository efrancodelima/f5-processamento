package br.com.fiap.soat.exception.messages;

public enum ApplicationMessage {
    
  ERRO_PROCESSAMENTO("Ocorreu um erro inesperado ao processar o vídeo. "
      + "Por favor, contate o suporte técnico."),
  
  PED_FINALIZADO("O pedido já foi finalizado."),
  PAG_PENDENTE("O pedido ainda não teve o pagamento aprovado.");


  private String mensagem;

  ApplicationMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
