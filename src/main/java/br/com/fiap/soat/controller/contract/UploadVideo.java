package br.com.fiap.soat.controller.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
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
        description = "No Content"),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = exemplo401)))
  })

  ResponseEntity<Object> uploadVideo(HttpServletRequest requisicao,
      @RequestParam("file") List<MultipartFile> video);

  String exemplo401 = """
      {
        "timestamp": 1742656750241,
        "status": 401,
        "error": "Unauthorized",
        "path": "/video/upload"
      }
      """;

}
