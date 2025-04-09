package br.com.fiap.soat.wrapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

class FileWrapperTest {

  AutoCloseable closeable;

  @Mock
  private MultipartFile file;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveConstruirFileWrapperComSucesso() throws IOException {
    byte[] fileContent = "content".getBytes();
    String fileName = "file.txt";

    when(file.getBytes()).thenReturn(fileContent);
    when(file.getOriginalFilename()).thenReturn(fileName);

    FileWrapper fileWrapper = new FileWrapper(file);

    assertArrayEquals(fileContent, fileWrapper.getContent());
    assertEquals(fileName, fileWrapper.getName());
  }

  @Test
  void deveDefinirContentVazioQuandoHouverIOException() throws IOException {
    String fileName = "file.txt";

    when(file.getBytes()).thenThrow(new IOException());
    when(file.getOriginalFilename()).thenReturn(fileName);

    FileWrapper fileWrapper = new FileWrapper(file);

    assertArrayEquals(new byte[0], fileWrapper.getContent());
    assertEquals(fileName, fileWrapper.getName());
  }

  @Test
  void deveDefinirNomeQuandoNaoHouverNome() throws IOException {
    byte[] fileContent = "content".getBytes();

    when(file.getBytes()).thenReturn(fileContent);
    when(file.getOriginalFilename()).thenReturn(null);

    FileWrapper fileWrapper = new FileWrapper(file);

    assertArrayEquals(fileContent, fileWrapper.getContent());
    assertEquals("unnamed_file", fileWrapper.getName());
  }

  @Test
  void deveSubstituirCharInvalidoNoNomeArquivo() throws IOException {
    byte[] fileContent = "test content".getBytes();
    String fileName = "t@e#s$t f&i*l(e).txt";

    when(file.getBytes()).thenReturn(fileContent);
    when(file.getOriginalFilename()).thenReturn(fileName);

    FileWrapper fileWrapper = new FileWrapper(file);

    assertArrayEquals(fileContent, fileWrapper.getContent());
    assertEquals("t_e_s_t_f_i_l_e_.txt", fileWrapper.getName());
  }
}
