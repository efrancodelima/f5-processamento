package br.com.fiap.soat.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HomeTest {

  @Test
  void deveRetornarLinkParaApi() {
    var controller = new Home();
    var msg = controller.showHome();
    assertTrue(msg.contains("/swagger-ui/index.html"));
  }
}
