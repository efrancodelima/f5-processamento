package br.com.fiap.soat.controller.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "Video")
public interface ListarVideos {

  @Operation(
      summary = "Listar vídeos",
      description = "Lista os vídeos do usuário logado, mostrando o status de cada um.")
  
  @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Ok",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = exemplo200))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = exemplo401)))
  })

  ResponseEntity<Object> listarVideos(HttpServletRequest requisicao);

  String exemplo200 = """
      [
        {
          "nomeVideo": "comunicado.docx",
          "statusProcessamento": "Finalizado com erro",
          "timestampStatus": "2025-02-23T14:12:39.184398",
          "mensagemErro": "O arquivo não é compatível com este serviço.",
          "linkDownload": null
        },
        {
          "nomeVideo": "super-heroes.mp4",
          "statusProcessamento": "Finalizado com sucesso",
          "timestampStatus": "2025-02-23T14:10:58.820325",
          "mensagemErro": null,
          "linkDownload": "https://nome-do-bucket.s3.amazonaws.com/nome-do-objeto?AWSAccessKeyId=EXEMPLODECHAVEDEACESSO&Expires=1672398725"
        }
      ]
      """;

  String exemplo401 = """
      {
        "timestamp": 1742656750241,
        "status": 401,
        "error": "Unauthorized",
        "path": "/video/listar"
      }
      """;
}
