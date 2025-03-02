package br.com.fiap.soat.exception;

public class NotFoundException extends Exception {

  public NotFoundException() {
    super("Nenhum pedido foi encontrado para o número informado.");
    // nesse microsserviço, só tem essa mensagem para esse tipo de exceção
  }
}
