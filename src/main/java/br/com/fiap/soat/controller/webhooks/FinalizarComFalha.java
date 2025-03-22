package br.com.fiap.soat.controller.webhooks;

import br.com.fiap.soat.service.provider.FinalizarComFalhaService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  @GetMapping(value = "/sucesso/{jobId}/{filePath}")
  public ResponseEntity<Void> listarVideos(@PathVariable("jobId") String jobId,
      @PathVariable("filePath") String filePath) {
    
    List<ProcessamentoDto> lista = service.execute(requisicao);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}