package br.com.fiap.soat.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerAplicacao {
  
  private static final Logger logger = LogManager.getLogger(LoggerAplicacao.class);

  public static void info(String msg) {
    logger.info(msg);
  }

  public static void error(String msg) {
    logger.error(msg);
  }
}
