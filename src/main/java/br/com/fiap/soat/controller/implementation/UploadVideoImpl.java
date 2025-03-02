package br.com.fiap.soat.controller.implementation;

import br.com.fiap.soat.controller.contract.UploadVideo;
import br.com.fiap.soat.service.provider.UploadVideoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
public class UploadVideoImpl implements UploadVideo {

  UploadVideoService service;

  @Autowired
  public UploadVideoImpl(UploadVideoService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Object> receberVideo(@RequestParam("file") List<MultipartFile> videos) {
    service.execute(videos);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}