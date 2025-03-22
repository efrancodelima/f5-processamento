package br.com.fiap.soat.controller.implementation;

import br.com.fiap.soat.controller.contract.ListarVideos;
import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.service.provider.ListarVideosService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class ListarVideosController implements ListarVideos {

  ListarVideosService service;

  @Autowired
  public ListarVideosController(ListarVideosService service) {
    this.service = service;
  }

  @Override
  @GetMapping(value = "/listar")
  public ResponseEntity<Object> listarVideos(HttpServletRequest requisicao) {

    List<ProcessamentoDto> lista = service.processarRequisicao(requisicao);
    return ResponseEntity.status(HttpStatus.OK).body(lista);
  }
}