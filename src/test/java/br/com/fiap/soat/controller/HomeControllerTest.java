package br.com.fiap.soat.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(HomeController.class)
@ContextConfiguration(classes = {HomeController.class})
class HomeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testShowHome() throws Exception {

    MvcResult mvcResult = this.mockMvc.perform(get("/")).andReturn();

    int statusCode = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    
    assertEquals(HttpStatus.OK.value(), statusCode);
    assertEquals(true, content.contains("Swagger UI"));
  }
}