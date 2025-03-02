package br.com.fiap.soat.util;

import br.com.fiap.soat.wrapper.FileWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SalvarArquivo {

  private SalvarArquivo() {}
  
  public static File salvar(FileWrapper arquivo, String diretorioStr)
      throws IOException {

    // Cria o diretório para o vídeo
    File diretorioVideo = new File(diretorioStr);
    if (!diretorioVideo.exists()) {
      diretorioVideo.mkdirs();
    }
    
    // Salva o arquivo no diretório
    String caminhoArquivoStr = diretorioStr + arquivo.getName();
    File videoSalvo = new File(caminhoArquivoStr);
    try (OutputStream os = new FileOutputStream(videoSalvo)) {
      os.write(arquivo.getContent());
    }

    return videoSalvo;
  }
}
