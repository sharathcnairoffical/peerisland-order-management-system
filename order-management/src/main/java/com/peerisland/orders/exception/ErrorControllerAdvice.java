package com.peerisland.orders.exception;

import com.peerisland.orders.dto.ApiRestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = CommonException.class)
    public ResponseEntity<?> handleCommonException(CommonException exception) {

        log.error("CommonException exception: {}", exception.getMessage());
        return sendErrorResponse(exception, HttpStatus.valueOf(exception.getStatus()));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException exception) {
        if(exception.getCause() == null){
            log.error("RuntimeException: {}", exception.getMessage());
        } else {
            log.error("RuntimeException Cause: {}", exception.getCause().getMessage());
        }
        return sendErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(InvalidDataException exception) {

        log.error("Invalid Data exception: {}", exception.getMessage());
        return sendErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException exception) {

        log.error("NotFoundException exception: {}", exception.getMessage());
        return sendErrorResponse(exception, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(value = UserExistsException.class)
    public ResponseEntity<?> handleUserExistsException(UserExistsException exception) {

        log.error("UserExistsException exception: {}", exception.getMessage());
        return sendErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = ServiceUnavailableException.class)
    public ResponseEntity<?> handleServiceUnavailableException(ServiceUnavailableException exception) {

        log.error("ServiceUnavailableException exception: {}", exception.getMessage());
        return sendErrorResponse(exception, HttpStatus.SERVICE_UNAVAILABLE);
    }


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
        log.error(String.valueOf(exception));
        log.error("Exception exception: {}", exception.getMessage());
        return sendErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<ApiRestResponse> sendErrorResponse(Exception exception, HttpStatus status) {

        String englishMessage = exception.getMessage();
        if (!(exception instanceof ServiceUnavailableException) && exception.getCause() != null) {
            englishMessage = exception.getCause().getMessage();
        }

        // Standardize unexpected errors
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            englishMessage = "Unexpected Error. Please try again later.";
        }

        ApiRestResponse response = new ApiRestResponse(englishMessage, Boolean.TRUE);
        return new ResponseEntity<>(response, status);
    }
}
