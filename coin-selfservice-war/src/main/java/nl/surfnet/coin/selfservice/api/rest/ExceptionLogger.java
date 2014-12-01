package nl.surfnet.coin.selfservice.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionLogger {
  private final static Logger logger = LoggerFactory.getLogger(ExceptionLogger.class);

  @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Unexpected exception occurred")
  @ExceptionHandler(Exception.class)
  public void logException(Exception exception) {
    logger.error("Unexpected exception occurred", exception);
  }
}
