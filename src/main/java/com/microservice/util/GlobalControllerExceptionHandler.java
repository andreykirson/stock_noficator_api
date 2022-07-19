package com.microservice.util;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.microservice.util.exceptions.BadRequest;
import com.microservice.util.exceptions.InvalidInput;
import com.microservice.util.exceptions.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(BadRequest.class)
  public @ResponseBody
  HttpErrorInfo handleBadRequestExceptions(
      ServerHttpRequest request, BadRequest ex) {

    return createHttpErrorInfo(BAD_REQUEST, request, ex);
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFound.class)
  public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
      ServerHttpRequest request, NotFound ex) {

    return createHttpErrorInfo(NOT_FOUND, request, ex);
  }

  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInput.class)
  public @ResponseBody HttpErrorInfo handleInvalidInputException(
      ServerHttpRequest request, InvalidInput ex) {

    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(
      HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {

    final String path = request.getPath().pathWithinApplication().value();
    final String message = ex.getMessage();

    LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
    return new HttpErrorInfo(httpStatus, path, message);
  }
}
