package br.com.fiap.soat.exception;

import br.com.fiap.soat.exception.messages.ApplicationMessage;

public class BusinessRuleException extends Exception {

  public BusinessRuleException(ApplicationMessage msg) {
    super(msg.getMessage());
  }
}
