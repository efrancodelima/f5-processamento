package br.com.fiap.soat.service.util;

import br.com.fiap.soat.config.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.GetJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.GetJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.Job;

@Service
public class ConsultarJobService {
  
  // Atributos
  private final AwsConfig awsConfig;

  // Construtor
  @Autowired
    public ConsultarJobService(AwsConfig awsConfig) {
    this.awsConfig = awsConfig;
  }

  // Método público
  public Job consultar(String jobId) {

    MediaConvertClient mediaConvertClient = awsConfig.buildMediaConvertClient();

    try {
      GetJobRequest request = GetJobRequest.builder().id(jobId).build();
      GetJobResponse response = mediaConvertClient.getJob(request);
      return response.job();
    
    } catch (Exception e) {
      System.err.println("Erro ao consultar o status do job: " + e.getMessage());
      throw new RuntimeException("Falha ao consultar o job", e);
    
    } finally {
      mediaConvertClient.close();
    }
  }
}
