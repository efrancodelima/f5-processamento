package br.com.fiap.soat.entity;

public enum StatusProcessamento {

  PENDENTE("Processando"),
  SUCESSO("Sucesso"),
  ERRO("Erro");

  private String mensagem;

  StatusProcessamento(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
