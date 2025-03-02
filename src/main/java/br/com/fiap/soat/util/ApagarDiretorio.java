package br.com.fiap.soat.util;

import java.io.File;

public class ApagarDiretorio {
  
  private ApagarDiretorio() {}

  public static void apagar(String diretorio) {
    apagar(new File(diretorio));
  }

  public static void apagar(File diretorio) {
    if (diretorio.isDirectory()) {
      File[] files = diretorio.listFiles();
      if (files != null) {
        for (File file : files) {
          apagar(file);
        }
      }
    }
    diretorio.delete();
  }
}
