package br.com.fiap.soat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

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
  private String arnRoleMediaConvert;
  
  public AwsBasicCredentials pegarCredenciais() {
    return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
  }

  public Region pegarRegiao() {
    return Region.of(region.toLowerCase());
  }
}
