package br.com.fiap.soat.wrapper;

import java.io.IOException;
import java.io.Serializable;
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
    this.name = file.getOriginalFilename();
  }

  public byte[] getContent() {
    return content;
  }

  public String getName() {
    return name;
  }
}
