package br.com.fiap.soat.wrapper;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

public class FileWrapper implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private final byte[] content;
  private final String name;
  
  public FileWrapper(MultipartFile file) {
    byte[] fileContent;
    try {
      fileContent = file.getBytes();
    } catch (IOException e) {
      fileContent = new byte[0];
    }
    
    this.content = fileContent;
    this.name = getFileName(file);
  }

  public byte[] getContent() {
    return content;
  }

  public String getName() {
    return name;
  }

  // Método privado
  // Evita path injection
  private String getFileName(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    
    if (fileName == null || fileName.isBlank()) {
      fileName = "unnamed_file";
    } else {
      fileName = fileName.replaceAll("[\\W&&[^\\.\\-]]", "_");
    }
    return Paths.get(fileName).getFileName().toString();
  }
}
