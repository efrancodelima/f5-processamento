package br.com.fiap.soat.config;

import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Component
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsConfig {

  // Atributos
  private String accountId;
  private String accessKeyId;
  private String secretAccessKey;
  private String region;
  private String bucketName;
  private String mediaConvertRoleArn;
  private String mediaConvertEndpoint;
  
  // Métodos públicos
  public Region obtainRegion() {
    return Region.of(region.toLowerCase());
  }

  public S3Client buildS3Client() {
    return S3Client.builder()
        .region(obtainRegion())
        .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
        .build();
  }
  
  public S3Presigner buildS3Presigner() {
    return S3Presigner.builder()
        .region(obtainRegion())
        .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
        .build();
  }

  public MediaConvertClient buildMediaConvertClient() {
    return MediaConvertClient.builder()
        .endpointOverride(URI.create(mediaConvertEndpoint))
        .build();
  }

  // Métodos privados
  private AwsBasicCredentials createCredentials() {
    return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
  }
}
