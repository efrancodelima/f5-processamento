package br.com.fiap.soat.validator;

import br.com.fiap.soat.exception.BadRequestException;
import br.com.fiap.soat.exception.messages.BadRequestMessage;
import java.util.List;

public class NumeroPedidoValidator {

  private NumeroPedidoValidator() {}

  public static void validar(Long numeroPedido) throws BadRequestException {

    if (numeroPedido == null) {
      throw new BadRequestException(BadRequestMessage.REQUISICAO_VAZIA);
    }

    if (numeroPedido < 1) {
      throw new BadRequestException(BadRequestMessage.REQUISICAO_VAZIA);
    }
  }

  public static void validar(List<Long> numerosPedidos) throws BadRequestException {

    if (numerosPedidos == null) {
      throw new BadRequestException(BadRequestMessage.REQUISICAO_VAZIA);
    }

    for (Long num : numerosPedidos) {
      validar(num);
    }
  }
}
