package br.com.fiap.soat.service.consumer;

import br.com.fiap.soat.config.AwsConfig;
import br.com.fiap.soat.util.LoggerAplicacao;
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
public class CriarJobService {

  // Atributos
  private static final int INTERVALO_CAPTURA = 15;

  private final AwsConfig awsConfig;

  // Construtor
  @Autowired
  public CriarJobService(AwsConfig awsConfig) {
    this.awsConfig = awsConfig;
  }
  
  // Método público
  public CreateJobResponse criarJob(String caminhoVideo, String diretorioImagens)
      throws Exception {

    caminhoVideo = getCaminhoVideo(caminhoVideo);
    diretorioImagens = getDiretorioImagens(diretorioImagens);

    MediaConvertClient mediaConvertClient = awsConfig.buildMediaConvertClient();

    try {
      CreateJobRequest createJobRequest = CreateJobRequest.builder()
          .role(awsConfig.getMediaConvertRoleArn())
          .settings(buildJobSettings(caminhoVideo, diretorioImagens))
          .build();

      return mediaConvertClient.createJob(createJobRequest);

    } catch (Exception e) {
      LoggerAplicacao.error(e.getMessage());
      throw new Exception(e.getMessage());
    
    } finally {
      mediaConvertClient.close();
    }
  }
  
  // Métodos privados
  private String getCaminhoVideo(String caminhoVideo) {
    return "s3://BUCKET/FILE_PATH"
        .replace("BUCKET", awsConfig.getBucketName())
        .replace("FILE_PATH", caminhoVideo);
  }

  private String getDiretorioImagens(String diretorioImagens) {
    return "s3://BUCKET/FOLDER_PATH"
        .replace("BUCKET", awsConfig.getBucketName())
        .replace("FOLDER_PATH", diretorioImagens);
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