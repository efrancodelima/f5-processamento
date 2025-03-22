package br.com.fiap.soat.exception;

import br.com.fiap.soat.exception.messages.ApplicationMessage;

public class ApplicationException extends Exception {

  public ApplicationException(ApplicationMessage enumMsg) {
    super(enumMsg.getMessage());
  }

  public ApplicationException(String msg) {
    super(msg);
  }
}