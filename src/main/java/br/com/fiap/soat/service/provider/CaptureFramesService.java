package br.com.fiap.soat.service.provider;

import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.messages.ApplicationMessage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CaptureFramesService {

  // Atributos
  private static final String TEMP_DIR = "/tmp/";
  private static final int INTERVALO = 15;

  // Método público
  public byte[] execute(@RequestParam("file") List<MultipartFile> videos)
      throws ApplicationException {
    
    // Processa os vídeos de forma assíncrona (ao mesmo tempo)
    List<CompletableFuture<String>> diretoriosImagensFuture = new ArrayList<>();
    List<String> nomesVideos = new ArrayList<>();

    for (MultipartFile file : videos) {
      diretoriosImagensFuture.add(processarVideo(file));
      nomesVideos.add(file.getOriginalFilename());
    }

    List<String> diretoriosImagens = new ArrayList<>();
    for (CompletableFuture<String> dir : diretoriosImagensFuture) {
      try {
        diretoriosImagens.add(dir.get());
      } catch (InterruptedException e) {
        // erro de thread
        throw new ApplicationException(ApplicationMessage.ERRO_PROCESSAMENTO);
      
      } catch (ExecutionException e) {
        // erro recebido do método processarVideo
        throw new ApplicationException(e.getMessage()
            .replace("java.lang.RuntimeException: ", ""));
      }
    }

    // Combina todos os diretórios de imagens em um arquivo zip
    String combinedZipDir = TEMP_DIR + UUID.randomUUID().toString();
    byte[] combinedZipFiles = null;
    try {
      combinedZipFiles = criarArquivoZip(diretoriosImagens, combinedZipDir, nomesVideos);
    
    } catch (IOException e) {
      throw new ApplicationException(ApplicationMessage.ERRO_PROCESSAMENTO);
    
    } finally {
      // Apaga os arquivos e diretórios temporários
      apagarDiretoriosTemporarios(diretoriosImagens);
      apagarDiretorioTemporario(combinedZipDir);
      System.out.println("Arquivos temporários apagados com sucesso.");
    }

    return combinedZipFiles;
  }

  // Métodos privados
  private CompletableFuture<String> processarVideo(MultipartFile file) {
    
    return CompletableFuture.supplyAsync(() -> {
      
      String uniqueId = UUID.randomUUID().toString();
      String nomeVideo = file.getOriginalFilename();
      String diretorioVideoStr = TEMP_DIR + uniqueId;
      String caminhoVideoStr = diretorioVideoStr + "/uploaded_video.mp4";
      String diretorioImagensStr = diretorioVideoStr + "/frames";

      // Verifica se o arquivo recebido está vazio
      if (file.isEmpty()) {
        throw new RuntimeException("Não foi possível ler o arquivo do vídeo " + nomeVideo);
      }
      
      try {
        // Cria os diretórios necessários
        File diretorioVideo = new File(diretorioVideoStr);
        if (!diretorioVideo.exists()) {
          diretorioVideo.mkdirs();
        }
        File caminhoVideo = new File(caminhoVideoStr);

        // Recebe o vídeo
        try (OutputStream os = new FileOutputStream(caminhoVideo)) {
          os.write(file.getBytes());
        }
        System.out.println("Arquivo de vídeo recebido com sucesso: " + nomeVideo);
        
        // Extrai as imagens
        extrairImagens(caminhoVideo, diretorioImagensStr);
        System.out.println("Imagens extraídas com sucesso: " + nomeVideo);

        // Retorna o diretório das imagens extraídas
        return diretorioImagensStr;

      } catch (Exception e) {
        if (e.getMessage().contains("Could not open input")) {
          throw new RuntimeException("O arquivo " + nomeVideo 
              + " não é compatível com este serviço.");
        } else {
          throw new RuntimeException("Erro ao processar o vídeo " + nomeVideo 
              + ". Por favor, contate o suporte técnico.");
        }
      
      } finally {
        // Apaga o vídeo recebido
        apagarDiretorioTemporario(caminhoVideoStr);
      }
    });
  }

  private void extrairImagens(File videoFile, String framesDirPath)
      throws IOException, FrameGrabber.Exception {
    
    var framesDir = new File(framesDirPath);
    if (!framesDir.exists()) {
      framesDir.mkdirs();
    }

    var grabber = new FFmpegFrameGrabber(videoFile);
    grabber.start();

    var converter = new Java2DFrameConverter();
    Frame frame;
    int frameNumber = 0;
    double frameRate = grabber.getFrameRate();
    int captureIntervalInFrames = (int) (frameRate * INTERVALO);

    try {
      while ((frame = grabber.grabImage()) != null) {
        if (frameNumber % captureIntervalInFrames == 0) {
          BufferedImage img = converter.convert(frame);
          if (img != null) {
            File output = new File(framesDir, "frame_" + frameNumber + ".jpg");
            ImageIO.write(img, "jpg", output);
          }
        }
        frameNumber++;
      }
    } finally {
      grabber.stop();
      grabber.close();
      converter.close();
    }
  }

  private byte[] criarArquivoZip(List<String> frameDirs, String combinedZipDir, 
      List<String> originalFilenames) throws IOException {
    
    String combinedZipFilePath = combinedZipDir + "/combined.zip";
    File combinedZipDirFile = new File(combinedZipDir);
    if (!combinedZipDirFile.exists()) {
      combinedZipDirFile.mkdirs();
    }

    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(combinedZipFilePath))) {
      for (int i = 0; i < frameDirs.size(); i++) {
        String frameDirPath = frameDirs.get(i);
        String originalFilename = originalFilenames.get(i);
        adicionarConteudoNoArquivoZip(zos, frameDirPath, originalFilename);
      }
    }

    return Files.readAllBytes(Paths.get(combinedZipFilePath));
  }

  private void adicionarConteudoNoArquivoZip(ZipOutputStream zos, String directoryPath,
      String parentDirectoryName) throws IOException {

    File directory = new File(directoryPath);
    for (File file : directory.listFiles()) {

      if (file.isDirectory()) {
        adicionarConteudoNoArquivoZip(zos, file.getPath(),
            parentDirectoryName + "/" + file.getName());

      } else {
        ZipEntry zipEntry = new ZipEntry(parentDirectoryName + "/" + file.getName());
        zos.putNextEntry(zipEntry);
        Files.copy(file.toPath(), zos);
        zos.closeEntry();
      }
    }
  }

  private void apagarDiretorioTemporario(String diretorio) {
    List<String> lista = new ArrayList<>();
    lista.add(diretorio);
    apagarDiretoriosTemporarios(lista);
  }
  
  private void apagarDiretoriosTemporarios(List<String> diretorios) {
    for (String dir : diretorios) {
      deleteDirectory(new File(dir));
    }
  }
  
  private void deleteDirectory(File directory) {
    if (directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteDirectory(file);
        }
      }
    }
    directory.delete();
  }
}
