package com.SecurityApp.advice;

import com.SecurityApp.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError>handleResourceNotFoundException(ResourceNotFoundException exception){
        ApiError apiError=new ApiError(exception.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        return  new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);

    }
}

