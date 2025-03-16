package br.com.fiap.soat.service.util;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.util.LoggerAplicacao;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.ContainerSettings;
import software.amazon.awssdk.services.mediaconvert.model.ContainerType;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.FileGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.FrameCaptureSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264RateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.H264Settings;
import software.amazon.awssdk.services.mediaconvert.model.Input;
import software.amazon.awssdk.services.mediaconvert.model.JobSettings;
import software.amazon.awssdk.services.mediaconvert.model.Output;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroup;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupType;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodec;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.VideoDescription;

/**
 * Extrai imagens do vídeo usando o AWS Elemental MediaConvert.
 */
@Service
public class ExtrairImagensService {

  // Atributos estáticos
  private static final String MEDIA_CONVERT_ENDPOINT
      = "https://lxlxpswfb.mediaconvert.us-east-1.amazonaws.com";

  private static final int INTERVALO_CAPTURA = 15;

  private final AwsConfig awsConfig;

  // Construtor
  @Autowired
  public ExtrairImagensService(AwsConfig awsConfig) {
    this.awsConfig = awsConfig;
  }
  
  // Método público
  // idVideo = idUsuario/numeroVideo-nomeVideo
  public CreateJobResponse iniciarJob(String idVideo) throws Exception {

    try {
      MediaConvertClient mediaConvertClient = MediaConvertClient.builder()
          .endpointOverride(new URI(MEDIA_CONVERT_ENDPOINT))
          .build();
    
      LoggerAplicacao.info("Extrair 1 OK");

      String caminhoVideo = getCaminhoVideo(idVideo);
      String diretorioImagens = getDiretorioImagens(idVideo);

      LoggerAplicacao.info("Extrair 2 OK");

      CreateJobRequest createJobRequest = CreateJobRequest.builder()
          .role(awsConfig.getArnRoleMediaConvert())
          .settings(buildJobSettings(caminhoVideo, diretorioImagens))
          .build();

      LoggerAplicacao.info("Extrair 3 OK");

      return mediaConvertClient.createJob(createJobRequest);

    } catch (Exception e) {
      LoggerAplicacao.error(e.getMessage());
      throw new Exception(e.getMessage());
    }
  }
  
  // Métodos privados
  private String getCaminhoVideo(String idVideo) {
    return String.format("s3://%s/%s", awsConfig.getBucketVideos(), idVideo);
  }

  private String getDiretorioImagens(String idVideo) {
    return String.format("s3://%s/frames/%s", awsConfig.getBucketImagens(),
        idVideo);
  }

  private JobSettings buildJobSettings(String inputFilePath, String outputFolderPath) {
    return JobSettings.builder()
      .inputs(buildInput(inputFilePath))
      .outputGroups(buildOutputGroup(outputFolderPath))
      .build();
  }
  
  private Input buildInput(String inputFilePath) {
    return Input.builder()
      .fileInput(inputFilePath)
      .build();
  }
  
  private OutputGroup buildOutputGroup(String diretorioSaida) {
    return OutputGroup.builder()
      .name("File Group")
      .outputGroupSettings(OutputGroupSettings.builder()
        .type(OutputGroupType.FILE_GROUP_SETTINGS)
        .fileGroupSettings(FileGroupSettings.builder()
          .destination(diretorioSaida)
          .build())
        .build())
      .outputs(

        // Output do vídeo
        Output.builder()
          .containerSettings(ContainerSettings.builder()
            .container(ContainerType.MP4)
            .build())
          .videoDescription(VideoDescription.builder()
            .codecSettings(VideoCodecSettings.builder()
              .codec(VideoCodec.H_264)
                .h264Settings(H264Settings.builder()
                  .rateControlMode(H264RateControlMode.CBR)
                  .bitrate(500 * 1000)
                .build())
              .build())
            .build())
          .build(),
        
        // Output das imagens
        Output.builder()
          .containerSettings(ContainerSettings.builder()
            .container(ContainerType.RAW)
            .build())
          .videoDescription(VideoDescription.builder()
            .codecSettings(VideoCodecSettings.builder()
              .codec(VideoCodec.FRAME_CAPTURE)
              .frameCaptureSettings(FrameCaptureSettings.builder()
                .framerateNumerator(1)
                .framerateDenominator(INTERVALO_CAPTURA)
                .build())
              .build())
            .build())
          .build()
      )
    .build();
  }
}