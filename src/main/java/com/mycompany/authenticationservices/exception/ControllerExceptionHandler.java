package com.mycompany.authenticationservices.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.authenticationservices.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author pervez
 * @version 1.0
 * @since 4 August, 2022
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();


    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noHandlerFoundException(NoHandlerFoundException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return new ErrorResponse("error",String.valueOf(HttpStatus.NOT_FOUND.value()), "No resource found for your request. Please verify you request", TIMESTAMP);

    }

    @ExceptionHandler({DataNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noHandlerFoundException(Exception ex) {
        log.error(ex.getMessage(), ex.getCause());
        return new ErrorResponse("error",String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage(), TIMESTAMP);

    }

    @ExceptionHandler({BadRequestException.class, DuplicateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse("error",String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage(), TIMESTAMP);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse notSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse("error",String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()), "Method Not Allowed. Please verify you request", TIMESTAMP);
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse BadCredentialsExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse("error",String.valueOf(HttpStatus.UNAUTHORIZED.value()), ex.getMessage(), TIMESTAMP);
    }
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse("error",String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage(), TIMESTAMP);
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConflict(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse("error", String.valueOf(HttpStatus.BAD_REQUEST.value()),ex.getMessage(),TIMESTAMP);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptionHandler(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ErrorResponse("error",String.valueOf(HttpStatus.BAD_REQUEST.value()), errors.toString(), TIMESTAMP);
    }

}
