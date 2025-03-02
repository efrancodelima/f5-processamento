package br.com.fiap.soat.controller.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Video")
public interface UploadVideo {

  @Operation(
      summary = "Enviar vídeos",
      description = "Envia um ou mais vídeos para processamento.")
  
  @ApiResponses(value = {
    @ApiResponse(
        responseCode = "204",
        description = "No Content")
  })

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  
  ResponseEntity<Object> receberVideo(@RequestParam("file") List<MultipartFile> video);

}
