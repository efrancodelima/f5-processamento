package br.com.fiap.soat.config;

import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
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

  private String accountId;
  private String accessKeyId;
  private String secretAccessKey;
  private String region;
  private String bucketName;
  private String mediaConvertRoleArn;
  private String mediaConvertEndpoint;
  
  @Bean
  @Scope("prototype")
  public S3Client s3Client() {
    return S3Client.builder()
        .region(obtainRegion())
        .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
        .build();
  }
  
  @Bean
  @Scope("prototype")
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .region(obtainRegion())
        .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
        .build();
  }

  @Bean
  @Scope("prototype")
  public MediaConvertClient mediaConvertClient() {
    return MediaConvertClient.builder()
        .endpointOverride(URI.create(mediaConvertEndpoint))
        .region(obtainRegion())
        .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
        .build();
  }

  // Métodos privados
  private Region obtainRegion() {
    return Region.of(region.toLowerCase());
  }

  private AwsBasicCredentials createCredentials() {
    return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
  }
}
