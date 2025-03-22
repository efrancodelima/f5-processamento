package br.com.fiap.soat.exception;

import br.com.fiap.soat.exception.messages.BadGatewayMessage;

public class BadGatewayException extends Exception {

  public BadGatewayException(BadGatewayMessage enumMsg) {
    super(enumMsg.getMessage());
  }
}