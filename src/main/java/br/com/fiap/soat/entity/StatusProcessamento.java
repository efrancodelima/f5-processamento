package br.com.fiap.soat.entity;

public enum StatusProcessamento {

  PENDENTE("Em processamento"),
  SUCESSO("Finalizado com sucesso"),
  ERRO("Finalizado com erro");

  private String mensagem;

  StatusProcessamento(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
