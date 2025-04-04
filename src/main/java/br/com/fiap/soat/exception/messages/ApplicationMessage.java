package br.com.fiap.soat.exception.messages;

public enum ApplicationMessage {
  
  lerArquivo("Ocorreu um erro ao ler o arquivo "),
  salvarVideo("Ocorreu um erro ao salvar o vídeo."),
  enviarS3("Ocorreu um erro ao enviar o vídeo para o S3."),
  criarJob("Ocorreu um erro ao criar o job no MediaConvert."),
  gerarLink("Ocorreu um erro ao gerar o link para download das imagens.");

  private String mensagem;

  ApplicationMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
