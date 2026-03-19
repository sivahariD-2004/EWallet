package com.example.WalletService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(feign.FeignException.NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFeignNotFound(feign.FeignException.NotFound ex) {
        return "Bank account does not exist";
    }

    @ExceptionHandler(feign.FeignException.BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFeignBadRequest(feign.FeignException.BadRequest ex) {
        return "Invalid bank operation";
    }
}
