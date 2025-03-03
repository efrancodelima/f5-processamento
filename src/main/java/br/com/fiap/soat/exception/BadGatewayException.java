package br.com.fiap.soat.exception;

public class BadGatewayException extends Exception {

  // Só tem uma mensagem para esse tipo de exceção
  public BadGatewayException() {
    super("Erro na comunicação com o microsserviço de comunicação.");
  }
}
