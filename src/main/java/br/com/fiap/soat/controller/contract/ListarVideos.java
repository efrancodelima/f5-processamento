package br.com.fiap.soat.controller.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

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
            examples = @ExampleObject(value = exemplo))
        ),
  })

  @GetMapping(value = "/listar")
  ResponseEntity<Object> listarVideos();


  String exemplo = """
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
}
