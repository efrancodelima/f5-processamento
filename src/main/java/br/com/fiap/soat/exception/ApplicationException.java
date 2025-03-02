package br.com.fiap.soat.exception;

import br.com.fiap.soat.exception.messages.ApplicationMessage;

public class ApplicationException extends Exception {

  public ApplicationException(ApplicationMessage message) {
    super(message.getMessage());
  }

  public ApplicationException(String message) {
    super(message);
  }
}
