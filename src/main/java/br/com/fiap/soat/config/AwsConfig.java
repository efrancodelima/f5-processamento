package br.com.fiap.soat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

@Component
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsConfig {

  private String accessKeyId;
  private String secretAccessKey;
  private String region;

  public AwsBasicCredentials pegarCredenciais() {
    return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
  }
}
