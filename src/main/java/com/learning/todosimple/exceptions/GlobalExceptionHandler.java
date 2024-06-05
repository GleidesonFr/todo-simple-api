package com.learning.todosimple.exceptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.learning.todosimple.services.exceptions.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_ECEPTION_HANDLER")
@RestControllerAdvice //avisa que essa é uma clase controller de advertência
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error. Check errors field for details");
        // for(FieldError fieldError : ex.getBindingResult().getFieldError()){

        // }
    }
}