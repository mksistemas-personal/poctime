package app.mkiniz.poctime.shared.adapter;

import app.mkiniz.poctime.shared.business.base.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> businessException(BusinessException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        StandardError err = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Business Rule Violation")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> validationException(BindException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Validation Rule Violation")
                .message(Objects.requireNonNull(e.getFieldError()).getDefaultMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(err);
    }
}
