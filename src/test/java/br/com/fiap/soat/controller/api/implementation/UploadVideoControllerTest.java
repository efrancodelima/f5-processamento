package br.com.fiap.soat.controller.api.implementation;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.fiap.soat.controller.api.filter.JwtAuthFilter;
import br.com.fiap.soat.controller.api.filter.JwtAuthFilterMock;
import br.com.fiap.soat.service.provider.UploadVideoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(UploadVideoController.class)
@ContextConfiguration(classes = { UploadVideoController.class, UploadVideoService.class })
class UploadVideoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtAuthFilter authFilter;

  @MockitoBean
  private UploadVideoService uploadService;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);
    mockAuthFilter();
    mockUploadService();
  }

  @Test
  public void testShowHome() throws Exception {
    this.mockMvc
        .perform(
          multipart("/video/upload")
          .file(getVideoFileMock())
          .header("Authorization", "Bearer xxxxx"))
        .andExpect(status().isNoContent());
  }

  // Métodos privados
  private MockMultipartFile getVideoFileMock() {
    return new MockMultipartFile(
      "file", 
      "video.mp4", 
      "video/mp4", 
      "conteúdo do vídeo".getBytes()
    );
  }

  private void mockAuthFilter() throws Exception {
    var filterMock = new JwtAuthFilterMock();
    doAnswer(invocation -> {
      HttpServletRequest request = invocation.getArgument(0);
      HttpServletResponse response = invocation.getArgument(1);
      FilterChain chain = invocation.getArgument(2);
      filterMock.doFilter(request, response, chain);
      return null;
    }).when(authFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());
  }

  private void mockUploadService() {
    doNothing().when(uploadService).processarRequisicao(Mockito.any(), Mockito.any());
  }
}
