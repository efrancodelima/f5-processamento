package br.com.fiap.soat.controller.implementation;

import br.com.fiap.soat.controller.contract.CaptureFrames;
import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.service.provider.CaptureFramesService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
public class CaptureFramesImpl implements CaptureFrames {

  CaptureFramesService service;

  @Autowired
  public CaptureFramesImpl(CaptureFramesService service) {
    this.service = service;
  }

  // Endpoint (método público)
  @Override
  public ResponseEntity<Object> videoUpload(@RequestParam("file") List<MultipartFile> videos)
      throws ApplicationException {

    byte[] arquivoZip = service.execute(videos);
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "imagens.zip");
    return new ResponseEntity<>(arquivoZip, headers, HttpStatus.OK);
  }
}