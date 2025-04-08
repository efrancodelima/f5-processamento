package br.com.fiap.soat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
  private String emailDestino;
  private String assunto;
  private String texto;

  public static EmailDto getEmailSucesso(String nomeArquivo,
      String emailDestino, String linkDownload) {

    String assunto = "Suas imagens estão prontas!";
    String texto = "<!DOCTYPE html><html><body>"
        + "<p>A captura de imagens do vídeo #VIDEO foi concluída com sucesso.</p>"
        + "<p>O link para download das imagens ficará disponível por 24 horas.</p>"
        + "<p>Link para download: "
        + "<a href=\"#LINK\">#LINK</a></p>"
        + "</body></html>";

    texto = texto.replace("#LINK", linkDownload).replace("#LINK", linkDownload);
    texto = texto.replace("#VIDEO", nomeArquivo);

    return new EmailDto(emailDestino, assunto, texto);
  }

  public static EmailDto getEmailFalha(String nomeArquivo,
      String emailDestino, String msgErro) {

    String assunto = "Seu vídeo não pode ser processado :(";
    String texto = "<!DOCTYPE html><html><body>"
        + "<p>A captura de imagens do vídeo #VIDEO falhou.</p>"
        + "<p>Motivo: #ERRO</p>"
        + "</body></html>";
        
    texto = texto.replace("#ERRO", msgErro);
    texto = texto.replace("#VIDEO", nomeArquivo);

    return new EmailDto(emailDestino, assunto, texto);
  }
}