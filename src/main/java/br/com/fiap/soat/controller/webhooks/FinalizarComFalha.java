package br.com.fiap.soat.controller.webhooks;

import br.com.fiap.soat.dto.FalhaDto;
import br.com.fiap.soat.service.provider.FinalizarComFalhaService;
import br.com.fiap.soat.util.LoggerAplicacao;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class FinalizarComFalha {

  FinalizarComFalhaService service;

  @Autowired
  public FinalizarComFalha(FinalizarComFalhaService service) {
    this.service = service;
  }

  @Hidden
  @PatchMapping(value = "/falha")
  public ResponseEntity<Void> finalizarComFalha(@RequestBody FalhaDto requisicao) {

    LoggerAplicacao.info("Recebeu uma falha: " + requisicao.toString());
    
    service.processarRequisicao(requisicao);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}