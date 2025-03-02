package br.com.fiap.soat.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompactarArquivos {

  private CompactarArquivos() {}
  
  public static void compactar(String diretorioArquivos, String caminhoArquivoZip)
      throws IOException {

    Path zipFile = Files.createFile(Paths.get(caminhoArquivoZip));

    try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFile))) {
      Path sourceDir = Paths.get(diretorioArquivos);
      Files.walk(sourceDir)
          .filter(path -> !Files.isDirectory(path))
          .forEach(path -> {
            String zipEntryName = sourceDir.relativize(path).toString();
            try {
              zs.putNextEntry(new ZipEntry(zipEntryName));
              Files.copy(path, zs);
              zs.closeEntry();
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          });
    }
  }
}
