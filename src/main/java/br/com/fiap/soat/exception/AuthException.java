package br.com.fiap.soat.exception;

import br.com.fiap.soat.exception.messages.AuthMessage;

public class AuthException extends Exception {

  public AuthException(AuthMessage enumMsg) {
    super(enumMsg.getMessage());
  }

  public AuthException(String msg) {
    super(msg);
  }
}