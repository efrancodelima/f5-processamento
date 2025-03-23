package br.com.fiap.soat.controller.webhooks;

import br.com.fiap.soat.dto.EmailDto;
import br.com.fiap.soat.dto.SucessoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class EmailTeste {

  @PatchMapping(value = "/teste")
  public ResponseEntity<Void> finalizarComSucesso(@RequestBody SucessoDto requisicao) {

    var x = EmailDto.getEmailSucesso("nomeArquivo", "emailDestino", "linkDownload");
    System.out.println(x.toString());

    var y = EmailDto.getEmailFalha("nomeArquivo", "emailDestino", "mensagemErro");
    System.out.println(y.toString());

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}