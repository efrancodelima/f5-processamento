package br.com.fiap.soat.util;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class ExtrairImagens {

  private ExtrairImagens() {}

  public static void extrair(File video, int intervalo, String diretorioImagens)
      throws Exception {
    
    var framesDir = new File(diretorioImagens);
    if (!framesDir.exists()) {
      framesDir.mkdirs();
    }

    var grabber = new FFmpegFrameGrabber(video);
    grabber.start();

    var converter = new Java2DFrameConverter();
    Frame frame;
    int frameNumber = 0;
    double frameRate = grabber.getFrameRate();
    int captureIntervalInFrames = (int) (frameRate * intervalo);

    try {
      while ((frame = grabber.grabImage()) != null) {
        if (frameNumber % captureIntervalInFrames == 0) {
          BufferedImage img = converter.convert(frame);
          if (img != null) {
            File output = new File(framesDir, "frame_" + frameNumber + ".jpg");
            ImageIO.write(img, "jpg", output);
          }
        }
        frameNumber++;
      }
    } finally {
      grabber.stop();
      grabber.close();
      converter.close();
    }
  }
}