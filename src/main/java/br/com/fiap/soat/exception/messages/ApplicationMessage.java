package br.com.fiap.soat.exception.messages;

public enum ApplicationMessage {
  
  LER_ARQUIVO("Ocorreu um erro ao ler o arquivo "),
  SALVAR_VIDEO("Ocorreu um erro ao salvar o vídeo."),
  ENVIAR_S3("Ocorreu um erro ao enviar o vídeo para o S3."),
  CRIAR_JOB("Ocorreu um erro ao criar o job no MediaConvert."),
  GERAR_LINK("Ocorreu um erro ao gerar o link para download das imagens.");

  private String mensagem;

  ApplicationMessage(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMessage() {
    return mensagem;
  }
}
