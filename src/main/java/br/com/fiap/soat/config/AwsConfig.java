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
  
  public S3Client buildS3Client() {
    return S3Client.builder()
        .region(obtainRegion())
        .credentialsProvider(StaticCredentialsProvider.create(createCredentials()))
        .build();
  }
  
  public Region obtainRegion() {
    return Region.of(region.toLowerCase());
  }

  public AwsBasicCredentials createCredentials() {
    return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
  }

  public MediaConvertClient buildMediaConvertClient() {
    return MediaConvertClient.builder()
        .endpointOverride(URI.create(mediaConvertEndpoint))
        .build();
  }
}
