package io.github.stellspec.console.web;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** API 异常处理器。 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 处理请求参数校验异常。
     *
     * @param exception 校验异常
     * @return API 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error(HttpStatus.BAD_REQUEST, "Invalid request", details);
    }

    /**
     * 处理 Elaticsearch IO 异常。
     *
     * @param exception IO 异常
     * @return API 错误响应
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiErrorResponse> handleIOException(IOException exception) {
        return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), Map.of("exception", exception.getClass().getName()));
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, Map<String, Object> details) {
        ApiErrorResponse response =
                new ApiErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, details);
        return ResponseEntity.status(status).body(response);
    }
}
