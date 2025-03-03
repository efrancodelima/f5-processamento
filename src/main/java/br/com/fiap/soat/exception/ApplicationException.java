package br.com.fiap.soat.exception;

public class ApplicationException extends Exception {

  // Só tem uma mensagem pré-definida para esse tipo de exceção
  public ApplicationException() {
    super("Ocorreu um erro inesperado ao processar o vídeo. "
        + "Por favor, contate o suporte técnico.");
  }

  public ApplicationException(String mensagem) {
    super(mensagem);
  }
}