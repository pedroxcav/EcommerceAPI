package com.ecommerce.api.exception.handler;

import com.ecommerce.api.exception.*;
import com.ecommerce.api.model.dto.exception.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({
            InvalidAmountException.class,
            InvalidCPFException.class,
            InvalidPriceException.class})
    private ResponseEntity<ExceptionDTO> invalidValueHandler(RuntimeException exception) {
        var exceptionResponseDTO = new ExceptionDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                "Invalid Value",
                exception.getMessage(),
                getTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDTO);
    }
    @ExceptionHandler({
            NullAddressException.class,
            NullNumberException.class,
            NullOrderException.class,
            NullProductException.class,
            NullUserException.class})
    private ResponseEntity<ExceptionDTO> nullEntityHandler(RuntimeException exception) {
        var exceptionResponseDTO = new ExceptionDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                "Non-existent Entity",
                exception.getMessage(),
                getTimestamp());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseDTO);
    }
    @ExceptionHandler({
            NumberRegisteredException.class,
            UserRegisteredException.class})
    private ResponseEntity<ExceptionDTO> entityRegisteredHandler(RuntimeException exception) {
        var exceptionResponseDTO = new ExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Entity Registered",
                exception.getMessage(),
                getTimestamp());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseDTO);
    }
    @ExceptionHandler(WishlistProductException.class)
    private ResponseEntity<ExceptionDTO> wishlistProductHandler(WishlistProductException exception) {
        var exceptionResponseDTO = new ExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Modify Wishlist",
                exception.getMessage(),
                getTimestamp());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseDTO);
    }
    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    private ResponseEntity<ExceptionDTO> stringIndexOutOfBoundsHandler(StringIndexOutOfBoundsException exception) {
        var exceptionResponseDTO = new ExceptionDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                "Invalid Length",
                exception.getMessage(),
                getTimestamp());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDTO);
    }

    private String getTimestamp() {
        var formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss a");
        return LocalDateTime.now().format(formatter);
    }
}