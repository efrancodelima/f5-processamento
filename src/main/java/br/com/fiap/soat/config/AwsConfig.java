package br.com.fiap.soat.config;

import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;

@Component
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsConfig {

  private String accountId;
  private String accessKeyId;
  private String secretAccessKey;
  private String region;
  private String bucketVideos;
  private String bucketImagens;
  private String bucketDownload;
  private String mediaConvertRoleArn;
  private String mediaConvertEndpoint;
  
  public AwsBasicCredentials createCredentials() {
    return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
  }

  public Region obtainRegion() {
    return Region.of(region.toLowerCase());
  }

  public MediaConvertClient buildMediaConvertClient() {
    return MediaConvertClient.builder()
        .endpointOverride(URI.create(mediaConvertEndpoint))
        .build();
  }
}
