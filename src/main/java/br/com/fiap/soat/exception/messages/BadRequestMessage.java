package br.com.fiap.soat.exception.messages;

public enum BadRequestMessage {
    
  REQUISICAO_VAZIA("Envie ao menos um vídeo para ser processado."),
  VIDEO_INVALIDO("O vídeo XXX é inválido.");


  private String mensagem;

  BadRequestMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
