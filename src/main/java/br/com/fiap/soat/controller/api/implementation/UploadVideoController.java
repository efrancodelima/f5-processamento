package br.com.fiap.soat.controller.api.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.soat.controller.api.contract.UploadVideo;
import br.com.fiap.soat.service.provider.UploadVideoService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/video")
public class UploadVideoController implements UploadVideo {

  UploadVideoService service;

  @Autowired
  public UploadVideoController(UploadVideoService service) {
    this.service = service;
  }

  @Override
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Object> uploadVideo(HttpServletRequest requisicao,
      @RequestParam("file") List<MultipartFile> videos) {

    service.receberUpload(requisicao, videos);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}