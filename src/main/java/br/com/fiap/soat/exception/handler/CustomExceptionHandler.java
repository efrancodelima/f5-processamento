package br.com.fiap.soat.exception.handler;

import br.com.fiap.soat.exception.ApplicationException;
import br.com.fiap.soat.exception.BadGatewayException;
import br.com.fiap.soat.exception.BadRequestException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public final ResponseEntity<CustomErrorResponse> handleResponseStatusException(
      BadRequestException ex, WebRequest request) {
      
    var httpstatus = HttpStatus.BAD_REQUEST;
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
}