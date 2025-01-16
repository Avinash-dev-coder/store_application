package com.grid.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Generic method to generate ErrorResponse
    private ResponseEntity<ErrorResponse> generateErrorResponse(String message, HttpStatus status, String errorCode) {
        ErrorResponse errorResponse = new ErrorResponse(message, status.value(), errorCode);
        return new ResponseEntity<>(errorResponse, status);
    }

    // Handling BadRequestException (400)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    // Handling NotFoundException (404)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    // Handling UnauthorizedException (401)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
    // Handling InternalServerErrorException (409)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, "CONFLICT");
    }

    // Handling InternalServerErrorException (500)
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    // Catch-all for any unexpected exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return generateErrorResponse("An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }



//    @ExceptionHandler(ProductNotFoundException.class)
//    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(CartNotFoundException.class)
//    public ResponseEntity<String> handleCartNotFoundException(CartNotFoundException ex){
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(ResponseStatusException.class)
//    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
//        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
//    }
//
//    @ExceptionHandler(InsufficientStockException.class)
//    public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(InvalidDetailsException.class)
//    public ResponseEntity<String> handleInvalidDetailsException(InvalidDetailsException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(UnauthorizedAccessException.class)
//    public ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGeneralException(Exception ex) {
//        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(InvalidProductException.class)
//    public ResponseEntity<String> handleInvalidProductException(InvalidProductException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
}
