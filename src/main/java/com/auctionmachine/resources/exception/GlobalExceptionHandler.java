package com.auctionmachine.resources.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auctionmachine.resources.schema.ExceptionResponse;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	 // JWTの有効期限切れエラーをキャッチ
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException ex) {
    	System.out.println("hogehoge");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionResponse("Token expired. Please login again."));
    }
    
    // `IllegalArgumentException` を `500 Internal Server Error` に変換
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse("Internal Server Error: " + ex.getMessage()));
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException ex){
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse("Not Found: " + ex.getMessage()));
    }
}