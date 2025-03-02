package br.com.fiap.soat.controller.implementation;

import br.com.fiap.soat.controller.contract.ListarVideos;
import br.com.fiap.soat.dto.ProcessamentoDto;
import br.com.fiap.soat.service.provider.ListarVideosService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class ListarVideosImpl implements ListarVideos {

  ListarVideosService service;

  @Autowired
  public ListarVideosImpl(ListarVideosService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Object> listarVideos() {
    List<ProcessamentoDto> lista = service.execute();
    return ResponseEntity.status(HttpStatus.OK).body(lista);
  }
}