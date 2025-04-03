package br.com.fiap.soat.exception.messages;

public enum ApplicationMessage {
  
  lerArquivo("Não foi possível ler o arquivo "),
  salvarVideo("Ocorreu um erro ao salvar o vídeo."),
  enviarS3("Ocorreu um erro ao enviar o vídeo para o S3."),
  criarJob("Ocorreu um erro ao criar o job no MediaConvert.");

  private String mensagem;

  ApplicationMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
