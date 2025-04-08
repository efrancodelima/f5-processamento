package br.com.fiap.soat.service.provider;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.soat.entity.UsuarioJpa;
import br.com.fiap.soat.service.other.ProcessarVideoService;
import br.com.fiap.soat.service.other.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

class UploadVideoServiceTest {

  AutoCloseable closeable;

  @Mock
  UsuarioService usuarioService;

  @Mock
  ProcessarVideoService processarVideoService;

  @Mock
  HttpServletRequest requisicao;

  @InjectMocks
  UploadVideoService service;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveRemoverNulosEncaminharOsVideosParaProcessar() {

    // Arrange
    var videos = getVideos();
    doReturn(new UsuarioJpa()).when(usuarioService).getUsuario(Mockito.any());
    doReturn(CompletableFuture.completedFuture(true))
        .when(processarVideoService).processar(Mockito.any(), Mockito.any());

    // Act
    service.receberUpload(requisicao, videos);

    // Assert
    verify(processarVideoService, times(2)).processar(Mockito.any(), Mockito.any());
  }

  // Método privado
  private List<MultipartFile> getVideos() {
    List<MultipartFile> multipartFiles = new ArrayList<>();

    multipartFiles.add(new MockMultipartFile("file1", "file1.txt", "text/plain",
        "Conteúdo do arquivo 1".getBytes()));
    
    multipartFiles.add(null);
    
    multipartFiles.add(new MockMultipartFile("file3", "file3.txt", "text/plain", 
        "Conteúdo do arquivo 3".getBytes()));

    return multipartFiles;
  }
}
