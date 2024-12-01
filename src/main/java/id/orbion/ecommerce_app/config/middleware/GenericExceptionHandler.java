package id.orbion.ecommerce_app.config.middleware;

import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

import id.orbion.ecommerce_app.common.error.BadRequestException;
import id.orbion.ecommerce_app.common.error.EmailAlreadyExistsException;
import id.orbion.ecommerce_app.common.error.InvalidPasswordException;
import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.common.error.RoleNotFoundException;
import id.orbion.ecommerce_app.common.error.UserNotFoundException;
import id.orbion.ecommerce_app.common.error.UsernameAlreadyExistsException;
import id.orbion.ecommerce_app.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GenericExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            UserNotFoundException.class,
            RoleNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleResourcErrorResponse(HttpServletRequest request,
            ResourceNotFoundException e) {
        return ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleBadRequestResponse(HttpServletRequest request, BadRequestException e) {
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleInternalServerErrorResponse(HttpServletRequest request, Exception e) {
        log.error("Internal Server Error", e);
        return ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleValidationException(HttpServletRequest request,
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(
                error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        String errorMessages = String.join(", ", errors.values());
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(errorMessages)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorResponse handleUnauthorizeException(HttpServletRequest request, Exception e) {
        return ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            UsernameAlreadyExistsException.class,
            EmailAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleConflictException(HttpServletRequest request, Exception e) {
        return ErrorResponse.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

}
