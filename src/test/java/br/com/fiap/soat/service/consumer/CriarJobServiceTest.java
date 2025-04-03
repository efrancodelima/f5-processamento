package br.com.fiap.soat.service.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.soat.config.AwsConfig;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;

class CriarJobServiceTest {

  AutoCloseable closeable;
  
  @Mock
  AwsConfig awsConfig;

  @Mock
  MediaConvertClient mediaConvertClient;

  @InjectMocks
  CriarJobService criarJobService;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void deveCriarJobComSucesso() throws Exception {

    // Arrange
    doReturn("bucket-name").when(awsConfig).getBucketName();
    doReturn("mc-role-arn").when(awsConfig).getMediaConvertRoleArn();
    
    CreateJobResponse createJobMock = CreateJobResponse.builder().build();
    doReturn(createJobMock).when(mediaConvertClient).createJob(any(CreateJobRequest.class));

    // Act
    String caminhoVideo = "video.mp4";
    String diretorioImagens = "imagens";
    CreateJobResponse response = criarJobService.criarJob(caminhoVideo, diretorioImagens);

    // Assert
    assertNotNull(response);
    assertEquals(createJobMock, response);

    verify(mediaConvertClient).createJob(any(CreateJobRequest.class));
    verify(mediaConvertClient).close();
  }
}