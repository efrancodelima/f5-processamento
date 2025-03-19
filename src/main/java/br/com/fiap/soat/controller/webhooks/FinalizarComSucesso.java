package br.com.fiap.soat.controller.webhooks;

import br.com.fiap.soat.dto.SucessoDto;
import br.com.fiap.soat.service.provider.FinalizarComSucessoService;
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
public class FinalizarComSucesso {

  FinalizarComSucessoService service;

  @Autowired
  public FinalizarComSucesso(FinalizarComSucessoService service) {
    this.service = service;
  }

  @Hidden
  @PatchMapping(value = "/sucesso")
  public ResponseEntity<Void> finalizarComSucesso(@RequestBody SucessoDto requisicao) {
    
    service.processarRequisicao(requisicao);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}