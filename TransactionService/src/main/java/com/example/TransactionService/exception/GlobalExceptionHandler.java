package com.example.TransactionService.exception;



import com.example.TransactionService.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestControllerAdvice
    public class GlobalExceptionHandler {

        // ✅ 1. Forbidden
        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("FORBIDDEN", ex.getMessage()));
        }

        // ✅ 2. Wallet not found (Feign)
        @ExceptionHandler(feign.FeignException.NotFound.class)
        public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", "Wallet not found"));
        }

        // ✅ 3. Other Feign errors
        @ExceptionHandler(feign.FeignException.class)
        public ResponseEntity<ErrorResponse> handleFeign(Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse("SERVICE_ERROR", "You are not allowed to view this account Transaction"));
        }

        // ❗ LAST (generic)
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ERROR", "Something went wrong"));
        }
    }
