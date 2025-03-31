package br.com.fiap.soat.controller.webhook;

import br.com.fiap.soat.dto.FalhaDto;
import br.com.fiap.soat.service.provider.FinalizarComFalhaService;
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
public class FalhaController {

  FinalizarComFalhaService service;

  @Autowired
  public FalhaController(FinalizarComFalhaService service) {
    this.service = service;
  }

  @Hidden
  @PatchMapping(value = "/falha")
  public ResponseEntity<Void> finalizarComFalha(@RequestBody FalhaDto requisicao) {

    service.processarRequisicao(requisicao);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}