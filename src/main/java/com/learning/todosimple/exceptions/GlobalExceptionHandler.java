package com.learning.todosimple.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.learning.todosimple.services.exceptions.DataBindingViolationException;
import com.learning.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice //avisa que essa é uma clase controller de advertência
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error. Check errors field for details");
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            error.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest webRequest){
        final String errorMessage = "Unknown error ocurred";
        log.error(errorMessage, exception);
        return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String errorMessage, HttpStatus internalServerError, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(internalServerError.value(), errorMessage);

        if(this.printStackTrace){
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(internalServerError).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationExpection(DataIntegrityViolationException dataIntegrityViolationException, WebRequest webRequest){
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);

        return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT, webRequest);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException constraintViolationException, WebRequest webRequest){
        log.error("Failed to validate element", constraintViolationException);
        return buildErrorResponse(constraintViolationException, HttpStatus.UNPROCESSABLE_ENTITY, webRequest);
    }

    public ResponseEntity<Object> buildErrorResponse(Exception exception, HttpStatus httpStatus, WebRequest webRequest){
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, webRequest);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException objectNotFoundException, WebRequest webRequest){
        log.error("Failed to find the requested element", objectNotFoundException);
        return buildErrorResponse(objectNotFoundException, HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationExpection(DataBindingViolationException dataBindingViolationException, WebRequest webRequest){
        log.error("Failed to save entity with associate data", dataBindingViolationException);
        return buildErrorResponse(dataBindingViolationException, HttpStatus.CONFLICT, webRequest);
    }
    
}