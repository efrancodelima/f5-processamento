package br.com.fiap.soat.controller.contract;

import br.com.fiap.soat.exception.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
public interface CaptureFrames {

  @Operation(
      summary = "Captura imagens de um vídeo",
      description = "Recebe um vídeo, captura as imagens do vídeo "
        + "a cada 15 segundos e retorna um arquivo zip com as imagens.")
  
  @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Ok",
        content = @Content(schema = @Schema(type = "string", format = "binary"))),

    @ApiResponse(
      responseCode = "422",
      description = "Unprocessable Entity",
      content = @Content(mediaType = "application/json",
      examples = @ExampleObject(value = """
          {
            "statusCode": 422,
            "error": "Unprocessable Entity",
            "message": "O arquivo tcc.pdf não é compatível com este serviço.",
            "timestamp": "2025-02-18T20:54:40.499424936",
            "path": "/video/captureframes"
          }
          """)))
  })

  @PostMapping(value = "/captureframes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  
  ResponseEntity<Object> videoUpload(@RequestParam("file") List<MultipartFile> video)
      throws ApplicationException;

}
