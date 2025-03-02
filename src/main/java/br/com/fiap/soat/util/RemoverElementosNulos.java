package br.com.fiap.soat.util;

import java.util.Iterator;
import java.util.List;

public class RemoverElementosNulos {

  private RemoverElementosNulos() {}

  public static <T> void remover(List<T> arquivos) {
    if (arquivos == null) {
      return;
    }

    Iterator<T> iterator = arquivos.iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == null) {
        iterator.remove();
      }
    }
  }
}
