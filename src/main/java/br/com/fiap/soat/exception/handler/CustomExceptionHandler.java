package br.com.fiap.soat.exception.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.AuthException;
import br.com.fiap.soat.exception.BadGatewayException;

@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(BadGatewayException.class)
  public final ResponseEntity<CustomErrorResponse> handleResponseStatusException(
      BadGatewayException ex, WebRequest request) {
      
    var httpstatus = HttpStatus.BAD_GATEWAY;
    var statusCode = httpstatus.value();
    var error = httpstatus.getReasonPhrase();

    var response = new CustomErrorResponse(
        statusCode,
        error,
        ex.getMessage(),
        LocalDateTime.now().toString(),
        request.getDescription(false).substring(4)
    );

    return ResponseEntity.status(httpstatus).body(response);
  }

  @ExceptionHandler(ApplicationException.class)
  public final ResponseEntity<CustomErrorResponse> handleResponseStatusException(
      ApplicationException ex, WebRequest request) {
    
    var httpstatus = HttpStatus.UNPROCESSABLE_ENTITY;
    var statusCode = httpstatus.value();
    var error = httpstatus.getReasonPhrase();

    var response = new CustomErrorResponse(
        statusCode,
        error,
        ex.getMessage(),
        LocalDateTime.now().toString(),
        request.getDescription(false).substring(4)
    );

    return ResponseEntity.status(httpstatus).body(response);
  }

  @ExceptionHandler(AuthException.class)
  public final ResponseEntity<CustomErrorResponse> handleResponseStatusException(
      AuthException ex, WebRequest request) {
    
    var httpstatus = HttpStatus.UNAUTHORIZED;
    var statusCode = httpstatus.value();
    var error = httpstatus.getReasonPhrase();

    var response = new CustomErrorResponse(
        statusCode,
        error,
        ex.getMessage(),
        LocalDateTime.now().toString(),
        request.getDescription(false).substring(4)
    );

    return ResponseEntity.status(httpstatus).body(response);
  }
}